package com.gmaslowski.distractor.remote

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp.{PeerClosed, Received, Write}
import akka.util.ByteString
import com.gmaslowski.distractor.core.api.DistractorApi.{Register, Unregister}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.gmaslowski.distractor.remote.RemoteReactorProxy.{CorrelationEntry, CorrelationId, TimedOut}

import scala.concurrent.duration._
import scala.language.postfixOps

object RemoteReactorProxy {
  def props(reactorRegistry: ActorRef) = Props(new RemoteReactorProxy(reactorRegistry))

  private[remote] type CorrelationId = UUID

  private case class CorrelationEntry(respondTo: ActorRef)

  private case class TimedOut(id: CorrelationId)

}

class RemoteReactorProxy(reactorRegistry: ActorRef) extends Actor with ActorLogging {

  val introductionRegex = "^name:\\s*(?<reactorName>\\w+)$".r
  var reactorName: String = _
  val responsePattern = "^correlationId:\\s*(?<correlationId>\\w{8}(?:-\\w{4}){3}-\\w{12})\\s*,\\s*data: (?<receivedData>.*)$".r
  val timeoutMessage = "{\"message\": \"Timed out, no response available.\"}"

  implicit val dispatcher = context dispatcher

  override def receive: Receive = waitForRegistration.orElse(handleConnectionClosed)

  private var requestResponseCorrelationMap: Map[CorrelationId, CorrelationEntry] = Map()

  def waitForRegistration: Receive = {
    case Received(data) =>
      val introduction = data.decodeString(UTF_8).dropRight(1)
      introduction match {
        case introductionRegex(reactorName) =>
          this.reactorName = reactorName
          log.info(s"Registering reactor under name $reactorName.")
          reactorRegistry ! Register(reactorName, self)
          scheduleHeartbeats()
          context become registeredReactor(sender()).orElse(handleConnectionClosed)
      }
  }

  def registeredReactor(connection: ActorRef): Receive = {
    case ReactorRequest(reactorId, data) =>
      val id = correlationId
      requestResponseCorrelationMap += (id -> CorrelationEntry(sender()))
      connection ! Write(createMessage(data, id))
      context.system.scheduler.scheduleOnce(timeout, self, TimedOut(id))

    case Received(data) =>
      val response = data.decodeString(UTF_8).dropRight(1)
      response match {
        case responsePattern(correlationId, receivedData) =>
          val id: CorrelationId = UUID.fromString(correlationId)
          val maybeEntry = requestResponseCorrelationMap.get(id)
          maybeEntry.foreach(entry => {
            val CorrelationEntry(respondTo) = entry
            requestResponseCorrelationMap -= id
            respondTo ! ReactorResponse(reactorName, receivedData)
          })
          if (maybeEntry.isEmpty) {
            log.debug(s"Got a message with nonexistent correlationId: $response")
          }

        case "heartbeat" =>
          log.info("Got heartbeat")

        case _ =>
          log.error(s"Got a message that could not be parsed: $response. Could not respond to the requester.")
      }

    case TimedOut(id) =>
      val maybeEntry = requestResponseCorrelationMap.get(id)
      maybeEntry.foreach(entry => {
        val CorrelationEntry(respondTo) = entry
        requestResponseCorrelationMap -= id
        respondTo ! ReactorResponse(reactorName, timeoutMessage)
      })
  }

  def handleConnectionClosed: Receive = {
    case PeerClosed =>
      if (reactorName != null) {
        log.info(s"Unregistering reactor with name ${this.reactorName}. Reason: TCP connection closed.")
        reactorRegistry ! Unregister(reactorName)
        context stop self
      }
  }

  def correlationId: CorrelationId = UUID.randomUUID()

  def createMessage(data: String, id: CorrelationId) = ByteString(s"correlationId: $id, data: $data\n", UTF_8.toString)

  def timeout = 5 seconds

  def scheduleHeartbeats(): Unit = {
    context.system.scheduler.schedule(30 seconds, 30 seconds, sender(), Write(ByteString("heartbeat\n", UTF_8.toString)))
  }
}


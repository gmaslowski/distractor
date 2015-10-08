package com.fp.distractor.core.reactor.info

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.Patterns
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.fp.distractor.core.reactor.info.InfoReactor.Information
import com.fp.distractor.core.transport.api.Message
import com.fp.distractor.core.transport.api.TransportApi.Say
import com.fp.distractor.registry.ActorRegistry.{GetRegisteredMsg, RegisteredMsg}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object InfoReactor {
  def response(appName: String, version: String, author: String): String = s"application: $appName, version: $version, author: $author"

  def props(information: Information, transportRegistry: ActorRef, reactorRegistry: ActorRef) =
    Props(classOf[InfoReactor], information, transportRegistry, reactorRegistry)

  case class Information(appName: String, version: String, author: String) {
    override def toString = response(appName, version, author)
  }
}

class InfoReactor(val information: Information,
                  val transportRegistry: ActorRef,
                  val reactorRegistry: ActorRef) extends Actor with ActorLogging {

  import scala.concurrent.duration._

  override def receive = {
    case reactorRequest: ReactorRequest =>
      implicit val ec = context.dispatcher

      val requestor = sender()

      // todo: reason about spawning stateful actor vs current-future-using solution
      val transportsFuture: Future[AnyRef] = Patterns.ask(transportRegistry, GetRegisteredMsg, 1 seconds)
      val reactorsFuture: Future[AnyRef] = Patterns.ask(reactorRegistry, GetRegisteredMsg, 1 seconds)

      Future.sequence(List(transportsFuture, reactorsFuture))
        .onComplete {
        case Success(response) =>

          // fixme: introduce reactor response approach -> https://waffle.io/gmaslowski/distractor/cards/55f1c907d599762f00a45cd0
          var responseString = information.toString

          response.foreach(
            x => {
              responseString = responseString.concat("\ntransports or reactors:\n")
              x.asInstanceOf[RegisteredMsg].list.foreach(
                registered => responseString = responseString.concat(s"$registered\n")
              )
            }
          )

          requestor forward new Say(new Message(reactorRequest.reactorId, responseString))
        case Failure(e) =>
          requestor forward new Say(new Message(reactorRequest.reactorId, e.getMessage))
      }

  }
}

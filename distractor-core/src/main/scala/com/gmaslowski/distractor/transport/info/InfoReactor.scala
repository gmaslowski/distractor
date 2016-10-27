package com.gmaslowski.distractor.transport.info

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.Patterns.ask
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegistered, RegisteredActors}
import com.gmaslowski.distractor.transport.info.InfoReactor.Information

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.Future.sequence
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

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  override def receive = {
    case reactorRequest: ReactorRequest =>
      implicit val ec = context.dispatcher

      val requestor = sender()

      // todo: reason about spawning stateful actor vs current-future-using solution
      val transportsFuture: Future[AnyRef] = ask(transportRegistry, GetRegistered, 1 seconds)
      val reactorsFuture: Future[AnyRef] = ask(reactorRegistry, GetRegistered, 1 seconds)

      sequence(List(transportsFuture, reactorsFuture))
        .onComplete {
          case Success(response) =>

            // fixme: introduce reactor response approach -> https://waffle.io/gmaslowski/distractor/cards/55f1c907d599762f00a45cd0
            val responseMap: mutable.Map[String, Object] = mutable.Map[String, Object](
              "appName" -> information.appName,
              "version" -> information.version,
              "author" -> information.author
            )

            response.foreach(
              x => responseMap += (x.asInstanceOf[RegisteredActors].registryName -> x.asInstanceOf[RegisteredActors].list)
            )

            requestor forward ReactorResponse(reactorRequest.reactorId, mapper.writeValueAsString(responseMap))
          case Failure(e) =>
            requestor forward ReactorResponse(reactorRequest.reactorId, mapper.writeValueAsString(Map("error" -> e.getMessage)))
        }

  }
}

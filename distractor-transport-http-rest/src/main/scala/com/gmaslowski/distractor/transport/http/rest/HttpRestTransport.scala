package com.gmaslowski.distractor.transport.http.rest

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.pattern.Patterns.ask
import akka.stream.ActorMaterializer
import com.gmaslowski.distractor.core.api.DistractorApi
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorResponse
import com.gmaslowski.distractor.transport.http.rest.HttpRestTransport.{HTTP_PORT, RestCommand}
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration.FiniteDuration

object HttpRestMarshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val restCommandFormat = jsonFormat1(RestCommand)
}


object HttpRestTransport {
  val HTTP_PORT: Int = 8080

  def props = Props[HttpRestTransport]

  final case class RestCommand(message: String)

}

class HttpRestTransport extends Actor with ActorLogging {

  import HttpRestMarshallers._

  implicit val materializer = ActorMaterializer()
  implicit val ec = context.dispatcher

  val route =
    (post & path("command") & entity(as[RestCommand])) { restCommand =>
      complete {
        val future = ask(context.actorSelection("akka://distractor/user/distractor/request-handler"),
          new DistractorApi.DistractorRequest(restCommand.message),
          FiniteDuration.apply(1, SECONDS))

        future.map[ToResponseMarshallable] {
          case ReactorResponse(reactorId, message) => message
        }
      }
    }

  Http(context.system).bindAndHandle(route, "localhost", HTTP_PORT)

  override def preStart() {
    // todo: fix that; should be provided via props, and not on preStart
    context.actorSelection("akka://distractor/user/distractor/transport-registry") ! Register("http-rest", self)
  }

  override def receive: Receive = {
    case ReactorResponse(reactorId, message) =>
  }
}




package com.gmaslowski.distractor.transport.http.rest

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
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

object HttpRestMarshallers extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val restCommandFormat = jsonFormat1(RestCommand)
}


object HttpRestTransport {
  val HTTP_PORT: Int = 8080

  def props(transportRegistry: ActorRef) = Props(classOf[HttpRestTransport], transportRegistry)

  final case class RestCommand(message: String)

}

class HttpRestTransport(transportRegistry: ActorRef) extends Actor with ActorLogging {

  import HttpRestMarshallers._

  import scala.concurrent.duration._

  implicit val materializer = ActorMaterializer()
  implicit val ec = context.dispatcher

  val route =
    (post & path("command") & entity(as[RestCommand])) { restCommand =>
      complete {
        val future = ask(context.actorSelection("akka://distractor/user/distractor/request-handler"),
          DistractorApi.DistractorRequest(restCommand.message),
          10 seconds)

        future.map[ToResponseMarshallable] {
          case ReactorResponse(reactorId, message) => message
        }
      }
    }

  Http(context.system).bindAndHandle(route, "0.0.0.0", HTTP_PORT)

  override def preStart() {
    transportRegistry ! Register("http-rest", self)
  }

  override def receive: Receive = {
    case ReactorResponse(reactorId, message) =>
  }
}




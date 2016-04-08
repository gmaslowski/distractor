package com.gmaslowski.distractor.transport.http.rest

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.pattern.Patterns.ask
import akka.stream.ActorMaterializer
import com.gmaslowski.distractor.core.api.DistractorApi
import com.gmaslowski.distractor.core.transport.api.TransportApi.Say
import com.gmaslowski.distractor.registry.ActorRegistry.RegisterMsg
import com.gmaslowski.distractor.transport.http.rest.HttpRestTransport.{HTTP_PORT, RestCommand}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Await
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

  val route =
    (post & path("rest-api") & entity(as[RestCommand])) { restCommand =>
      val future = ask(context.actorSelection("akka://distractor/user/distractor/request-handler"),
        new DistractorApi.DistractorRequest(restCommand.message),
        FiniteDuration.apply(1, SECONDS))


      val result = Await.result(future, FiniteDuration.apply(1, SECONDS)).asInstanceOf[Say]
      complete(HttpEntity(result.message.toString))
    }

  Http(context.system).bindAndHandle(route, "localhost", HTTP_PORT)

  override def preStart() {
    context.actorSelection("akka://distractor/user/distractor/transport-registry") ! RegisterMsg("http-rest", self)
  }

  override def receive: Receive = {
    case Say(message) =>

  }
}




package com.gmaslowski.distractor.transport.slack.http

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.pattern.Patterns.ask
import akka.stream.ActorMaterializer
import com.gmaslowski.distractor.core.api.DistractorApi
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorResponse
import com.gmaslowski.distractor.transport.slack.http.SlackHttpTransport.{HTTP_PORT, makeDistractorCommand}

import scala.concurrent.duration.FiniteDuration

object SlackHttpTransport {
  val HTTP_PORT: Int = 8081

  def props = Props[SlackHttpTransport]

  def makeDistractorCommand(slackCommand: String): String = "/" +
    slackCommand.split("&")
      .map(keyVal => (keyVal.split("=")(0), keyVal.split("=")(1)))
      .toMap
      .apply("text")
      .replaceAll("\\+", " ")

}

class SlackHttpTransport extends Actor with ActorLogging {

  implicit val materializer = ActorMaterializer()
  implicit val ec = context.dispatcher

  val route =
    (post & path("command") & entity(as[String])) { slackCommand =>
      complete {

        log.info(slackCommand)

        val command: String = makeDistractorCommand(slackCommand)

        val future = ask(context.actorSelection("akka://distractor/user/distractor/request-handler"),
          new DistractorApi.DistractorRequest(command),
          FiniteDuration.apply(10, SECONDS))

        future.map[ToResponseMarshallable] {
          case ReactorResponse(reactorId, message) => {
            HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`application/json`), s"""{"response_type": "in_channel", "text": "${message.replaceAll("\"","")}"}"""))
          }
        }
      }
    }

  Http(context.system).bindAndHandle(route, "0.0.0.0", HTTP_PORT)

  override def preStart() {
    // todo: fix that; should be provided via props, and not on preStart
    context.actorSelection("akka://distractor/user/distractor/transport-registry") ! Register("slack-http", self)
  }

  override def receive: Receive = {
    case ReactorResponse(reactorId, message) =>
  }
}

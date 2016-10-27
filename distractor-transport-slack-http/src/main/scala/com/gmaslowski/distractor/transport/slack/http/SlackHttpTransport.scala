package com.gmaslowski.distractor.transport.slack.http

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.gmaslowski.distractor.core.api.DistractorApi.{DistractorRequest, Register}
import com.gmaslowski.distractor.transport.slack.http.SlackHttpTransport.{HTTP_PORT, makeDistractorCommand, makeResponseUrl}
import com.gmaslowski.distractor.transport.slack.http.SlackWebHookCallback.Terminate
import org.apache.commons.codec.net.URLCodec.decodeUrl
import play.api.libs.ws.ahc.AhcWSClient

object SlackHttpTransport {
  val HTTP_PORT: Int = 8081

  def props(transportRegistry: ActorRef, ahcWSClient: AhcWSClient) = Props(classOf[SlackHttpTransport], transportRegistry, ahcWSClient)

  def makeDistractorCommand(slackCommand: String): String = {
    val command = slackCommand.split("&")
      .map(keyVal => (keyVal.split("=")(0), keyVal.split("=")(1)))
      .toMap
      .apply("text")

    "/" + new String(decodeUrl(command.getBytes))
  }

  def makeResponseUrl(slackCommand: String): String =
    new String(decodeUrl(slackCommand.split("&")
      .map(keyVal => (keyVal.split("=")(0), keyVal.split("=")(1)))
      .toMap
      .apply("response_url").getBytes))

}

class SlackHttpTransport(val transportRegistry: ActorRef, val ahcWSClient: AhcWSClient) extends Actor with ActorLogging {

  implicit val materializer = ActorMaterializer()
  implicit val ec = context.dispatcher

  val route =
    (post & path("command") & entity(as[String])) { slackMessageBody =>
      complete {
        val command = makeDistractorCommand(slackMessageBody)
        val responseUrl = makeResponseUrl(slackMessageBody)

        fireDistractorCommand(command, responseUrl, ahcWSClient)

        HttpResponse(200, entity = HttpEntity(ContentType(MediaTypes.`application/json`),s"""{\"response_type\": \"in_channel\"}"""))
      }
    }

  def fireDistractorCommand(command: String, responseUrl: String, ahcWSClient: AhcWSClient): Unit = {
    import scala.concurrent.duration._

    val slackWebHookCallback = context.actorOf(Props(new SlackWebHookCallback {
      override val client = ahcWSClient
      override val slackWebHook = responseUrl

      context.actorSelection("akka://distractor/user/distractor/request-handler") ! DistractorRequest(command)
    }))

    context.system.scheduler.scheduleOnce(30 seconds, slackWebHookCallback, Terminate)
  }

  Http(context.system).bindAndHandle(route, "0.0.0.0", HTTP_PORT)

  override def preStart() {
    transportRegistry ! Register("slack-http", self)
  }

  override def receive: Receive = {
    case _ =>
  }

}

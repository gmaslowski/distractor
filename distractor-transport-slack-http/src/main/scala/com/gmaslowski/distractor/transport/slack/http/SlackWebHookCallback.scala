package com.gmaslowski.distractor.transport.slack.http

import akka.actor.Actor
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorResponse
import com.gmaslowski.distractor.transport.slack.http.SlackWebHookCallback.{Terminate, formatMessage}
import play.api.libs.ws.ahc.AhcWSClient
import spray.json.JsonParser

object SlackWebHookCallback {

  case object Terminate

  def formatMessage(json: String) = JsonParser(json).prettyPrint.replaceAll("\"", "")

}

trait SlackWebHookCallback extends Actor {

  implicit val ec = context.dispatcher

  val client: AhcWSClient
  val slackWebHook: String

  override def receive: Receive = {
    case ReactorResponse(reactorId, message) =>
      client
        .url(slackWebHook)
        .withHeaders("Accept" -> "application/json")
        .post(s"""{"response_type": "in_channel", "text": "```${formatMessage(message)}```"}""")

      self ! Terminate

    case Terminate =>
      context.stop(self)
  }

}

package com.gmaslowski.distractor.reactor.foaas

import akka.actor.{Actor, ActorLogging, Props}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.AhcWSClient

object FoaasReactor {

  def props(ahcWSClient: AhcWSClient) = Props(classOf[FoaasReactor], ahcWSClient)
}

class FoaasReactor(val ahcWsClient: AhcWSClient) extends Actor with ActorLogging {

  implicit val ec = context.dispatcher

  override def receive = {
    case ReactorRequest(reactorId, data, passThrough) =>

      val sender = context.sender()

      ahcWsClient
        .url(s"https://www.foaas.com/${data}")
        .withHeaders("Accept" -> "application/json")
        .get()
        .onSuccess {
          case result =>
            sender forward ReactorResponse(reactorId, result.body)
        }
  }
}

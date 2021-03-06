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
    case ReactorRequest(reactorId, data) =>

      val sender = context.sender()

      val f = ahcWsClient
        .url(s"https://www.foaas.com/${data}")
        .withHeaders("Accept" -> "application/json")
        .get()

      f onSuccess {
        case result =>
          sender forward ReactorResponse(reactorId, result.body)
      }

      f onFailure {
        case t => log.error(t, "err")
      }
  }
}

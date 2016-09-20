package com.gmaslowski.distractor.reactor.foaas

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.{AhcConfigBuilder, AhcWSClient}

object FoaasReactor {

  def props = Props[FoaasReactor]
}

class FoaasReactor extends Actor with ActorLogging {

  implicit val mat = ActorMaterializer.apply(ActorMaterializerSettings.create(context.system))
  implicit val ec = context.dispatcher

  val client = new AhcWSClient(new AhcConfigBuilder().build())

  override def postStop = {
    client.close()
  }

  override def receive = {
    case ReactorRequest(reactorId, data) =>

      val sender = context.sender()

      client
        .url(s"https://www.foaas.com/${data}")
        .withHeaders("Accept" -> "application/json")
        .get()
        .onSuccess {
          case result =>
            sender forward ReactorResponse(reactorId, result.body)
        }
  }
}

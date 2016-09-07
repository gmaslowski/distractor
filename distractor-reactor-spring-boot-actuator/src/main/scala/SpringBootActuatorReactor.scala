package com.gmaslowski.distractor.reactor.spring.boot.actuator

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.{AhcConfigBuilder, AhcWSClient}

object SpringBootActuatorReactor {

  def props = Props[SpringBootActuatorReactor]
}

class SpringBootActuatorReactor extends Actor with ActorLogging {

  override def receive = {
    case reactorRequest: ReactorRequest =>
      implicit val mat = ActorMaterializer.apply(ActorMaterializerSettings.create(context.system))
      implicit val ec = context.dispatcher

      val client = new AhcWSClient(new AhcConfigBuilder().build())
      val sender = context.sender()

      client
        .url(s"${sys.env("SPRING_BOOT_ACTUATOR_LINK")}/env")
        .get()
        .onSuccess {
          case result =>
            sender forward ReactorResponse(reactorRequest.reactorId, result.body)
            client.close()
        }
  }
}

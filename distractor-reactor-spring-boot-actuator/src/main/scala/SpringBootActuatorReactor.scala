package com.gmaslowski.distractor.reactor.spring.boot.actuator

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.{AhcConfigBuilder, AhcWSClient}

object SpringBootActuatorReactor {

  def props = Props[SpringBootActuatorReactor]
}

class SpringBootActuatorReactor extends Actor with ActorLogging {

  val apps: Map[String, String] = sys.env("SPRING_BOOT_ACTUATORS")
    .split(",")
    .map(keyVal => (keyVal.split("=")(0), keyVal.split("=")(1)))
    .toMap

  override def receive = {
    case ReactorRequest(reactorId, data) =>
      implicit val mat = ActorMaterializer.apply(ActorMaterializerSettings.create(context.system))
      implicit val ec = context.dispatcher

      val client = new AhcWSClient(new AhcConfigBuilder().build())
      val sender = context.sender()

      val appName: String = apps(data.split(" ")(0))
      val springCommand: String = data.split(" ")(1)

      client
        .url(s"${appName}/${springCommand}")
        .get()
        .onSuccess {
          case result =>
            sender forward ReactorResponse(reactorId, result.body)
            client.close()
        }
  }
}

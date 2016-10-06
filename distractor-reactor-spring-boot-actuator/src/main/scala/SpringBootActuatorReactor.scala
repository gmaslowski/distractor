package com.gmaslowski.distractor.reactor.spring.boot.actuator

import akka.actor.{Actor, ActorLogging, Props}
import com.fasterxml.jackson.databind.ObjectMapper
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.AhcWSClient

object SpringBootActuatorReactor {

  def props(ahcWSClient: AhcWSClient, mapper: ObjectMapper) = Props(classOf[SpringBootActuatorReactor], ahcWSClient, mapper)
}

class SpringBootActuatorReactor(val client: AhcWSClient, val mapper: ObjectMapper) extends Actor with ActorLogging {

  implicit val ec = context.dispatcher

  val listCommand = "(list)".r

  val apps: Map[String, String] = sys.env.get("SPRING_BOOT_ACTUATORS").getOrElse("dummy=http://example.com")
    .split(",")
    .map(keyVal => (keyVal.split("=")(0), keyVal.split("=")(1)))
    .toMap

  override def receive = {
    case ReactorRequest(reactorId, data, passThrough) =>
      data match {
        case listCommand(list) =>
          context.sender() forward ReactorResponse(reactorId, mapper.writeValueAsString(apps))

        case _ =>
          val sender = context.sender()

          val appName: String = apps(data.split(" ")(0))
          val springCommand: String = data.split(" ")(1)

          client
            .url(s"${appName}/${springCommand}")
            .get()
            .onSuccess {
              case result =>
                sender forward ReactorResponse(reactorId, result.body)
            }
      }
  }
}

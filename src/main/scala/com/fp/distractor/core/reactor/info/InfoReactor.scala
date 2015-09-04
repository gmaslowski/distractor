package com.fp.distractor.core.reactor.info

import akka.actor.{Actor, ActorLogging, Props}
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.fp.distractor.core.reactor.info.InfoReactor.Information

object InfoReactor {

  val INFO_REACTOR_NAME = "info"

  def props(information: Information) = Props(classOf[InfoReactor], information)

  case class Information(appName: String, version: String, author: String)
}

class InfoReactor(val information: Information) extends Actor with ActorLogging {

  override def receive = {
    case reactorRequest: ReactorRequest =>
      log.info(reactorRequest.toString)
      log.info(information.toString)
  }
}

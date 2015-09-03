package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fp.distractor.core.ReactorTransportMixer.React
import com.fp.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.fp.distractor.core.transport.api.TransportApi.Say

import scala.util.matching.Regex.MatchIterator

object ReactorTransportMixer {

  def props(reactorRegistry: ActorRef, transportRegistry: ActorRef) = Props(classOf[ReactorTransportMixer], reactorRegistry, transportRegistry)

  case class React(msg: String)
}

class ReactorTransportMixer(val reactorRegistry: ActorRef, val transportRegistry: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case react: React =>
      val pattern = "(/{1})([a-zA-Z]+)(\\s{1})(.*)".r
      val matcher: MatchIterator = pattern.findAllIn(react.msg)

      log.info(matcher.toString())
      log.info(matcher.group(0))
      log.info(matcher.group(1))
      log.info(matcher.group(2))
      log.info(matcher.group(3))

      val reactorId = matcher.group(2)
      val data = matcher.group(4)

      reactorRegistry forward new ReactorRequest(reactorId, data)

    case say: Say =>
      sender() ! new ReactorResponse("sth", say.message.msg)
  }
}

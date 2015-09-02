package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fp.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}

object ReactorTransportMixer {

  def props(reactorRegistry: ActorRef, transportRegistry: ActorRef) = Props(classOf[ReactorTransportMixer], reactorRegistry, transportRegistry)
}

class ReactorTransportMixer(val reactorRegistry: ActorRef, val transportRegistry: ActorRef) extends Actor with ActorLogging {

  override def receive = {
    case ReactorRequest(reactorId, data) => reactorRegistry forward ReactorRequest(reactorId, data)
    case ReactorResponse(reactorId, data) => transportRegistry forward ReactorResponse(reactorId, data)
  }
}

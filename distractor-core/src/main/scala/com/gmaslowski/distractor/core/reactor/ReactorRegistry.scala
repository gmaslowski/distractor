package com.gmaslowski.distractor.core.reactor

import akka.actor.{Actor, ActorLogging, Props}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi
import ReactorApi.ReactorRequest
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.gmaslowski.distractor.registry.ActorRegistry

object ReactorRegistry {
  def props = Props[ReactorRegistry]
}

class ReactorRegistry extends Actor with ActorLogging with ActorRegistry {

  override def receive = handleRegistry orElse (handle)

  def handle: Receive = {
    case reactorRequest: ReactorRequest =>
      registry.get(reactorRequest.reactorId) getOrElse context.system.deadLetters forward reactorRequest
  }

  override def registryName: String = "reactors"
}

package com.gmaslowski.distractor.reactor.system

import akka.actor.{Actor, ActorLogging, Props}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.gmaslowski.distractor.core.transport.api.Message
import com.gmaslowski.distractor.core.transport.api.TransportApi.Say

object SystemReactor {

  def props = Props[SystemReactor]
}

class SystemReactor extends Actor with ActorLogging {
  override def receive = {
    case reactorRequest: ReactorRequest =>
      val shellCommand = reactorRequest.data

      // fixme: better to do it in a future/actor
      val shellCommandResponse = shellCommand !!

      context.sender() forward new Say(new Message(reactorRequest.reactorId, shellCommandResponse))
  }
}

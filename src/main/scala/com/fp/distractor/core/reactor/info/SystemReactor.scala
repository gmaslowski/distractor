package com.fp.distractor.core.reactor.system

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.fp.distractor.core.transport.api.TransportApi.{Message, Say}

import scala.sys.process._

object SystemReactor {

  def props(mixer: ActorRef) = Props(classOf[SystemReactor], mixer)
}

class SystemReactor(val mixer: ActorRef) extends Actor with ActorLogging {
  override def receive = {
    case reactorRequest: ReactorRequest =>
      val shellCommand = reactorRequest.data
      val shellCommandResponse = shellCommand !!

      mixer forward new Say(new Message(shellCommandResponse))
  }
}

package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fp.distractor.core.ReactorTransportMixer.{React, extractReactorAndCommandFrom}
import com.fp.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.fp.distractor.core.transport.api.TransportApi.Say
import com.fp.distractor.registry.ActorRegistry.{RegisterMsg, RegisteredMsg}

object ReactorTransportMixer {

  def props(reactorRegistry: ActorRef) = Props(classOf[ReactorTransportMixer], reactorRegistry)

  case class React(msg: String)

  private val COMMAND_PATTERN = """\/([a-zA-Z]+)\s*(.*)"""

  def extractReactorAndCommandFrom(message: String): (String, String) = {
    val Pattern = COMMAND_PATTERN.r
    val Pattern(reactorId, command) = message

    (reactorId, command)
  }
}

class ReactorTransportMixer(val reactorRegistry: ActorRef) extends Actor with ActorLogging {
  override def receive = {

    case react: React =>
      val (reactorId: String, data: String) = extractReactorAndCommandFrom(react.msg)
      reactorRegistry forward new ReactorRequest(reactorId, data)

    case say: Say =>
      sender() ! new ReactorResponse("sth", say.message.command)
  }
}

package com.gmaslowski.distractor.core.api

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gmaslowski.distractor.core.api.DistractorApi.DistractorRequest
import com.gmaslowski.distractor.core.api.DistractorRequestHandler.extractReactorAndCommandFrom
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest

object DistractorRequestHandler {

  def props(reactorRegistry: ActorRef) = Props(classOf[DistractorRequestHandler], reactorRegistry)

  private val COMMAND_PATTERN = """\/([a-zA-Z]+)\s*(.*)"""

  def extractReactorAndCommandFrom(message: String): (String, String) = {
    val Pattern = COMMAND_PATTERN.r
    val Pattern(reactorId, command) = message

    (reactorId, command)
  }
}

class DistractorRequestHandler(val reactorRegistry: ActorRef) extends Actor with ActorLogging {
  override def receive = {

    case distractorRequest: DistractorRequest =>
      val (reactorId: String, data: String) = extractReactorAndCommandFrom(distractorRequest.commandMessage)
      reactorRegistry forward new ReactorRequest(reactorId, data)
  }
}

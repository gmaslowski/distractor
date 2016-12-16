package com.gmaslowski.distractor.transport.telnet

import akka.actor.{ActorRef, ActorSelection}
import akka.event.LoggingAdapter
import com.gmaslowski.distractor.core.api.DistractorApi

class TelnetHandler(val log: LoggingAdapter, val distractor: ActorSelection, val telnetTransport: ActorRef) extends IoHandlerAdapter {

  @throws(classOf[Exception])
  override def messageReceived(session: IoSession, message: AnyRef) {
    distractor.tell(new DistractorApi.DistractorRequest(message.asInstanceOf[String]), telnetTransport)
  }
}
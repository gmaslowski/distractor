package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fp.distractor.core.reactor.ReactorManager
import com.fp.distractor.core.transport.TransportManager

object Distractor {
  def props = Props[Distractor]
}

class Distractor extends Actor with ActorLogging {

  var transportManager: ActorRef = context.system.deadLetters
  var reactorManager: ActorRef = context.system.deadLetters

  def receive = {
    // fixme: that shouldn't look like this
    case AnyRef =>
  }

  override def preStart() = {
    transportManager = context.actorOf(TransportManager.props)
    reactorManager = context.actorOf(ReactorManager.props)
  }
}

package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, Props}
import com.fp.distractor.core.TransportManager.SendMsg
import com.fp.distractor.registry.ActorRegistry

object TransportManager {
  def props = Props[TransportManager]

  case class SendMsg(msg: String)
}

class TransportManager extends Actor with ActorLogging with ActorRegistry {

  override def receive = {
    case SendMsg(msg) => log.info(msg)
  }
}
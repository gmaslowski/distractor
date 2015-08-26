package com.fp.distractor.core

import akka.actor.{Actor, ActorLogging, Props}
import com.fp.distractor.registry.ActorRegistry

object ReactorManager {
  def props = Props[ReactorManager]
}

class ReactorManager extends Actor with ActorLogging with ActorRegistry {

  override def receive = {
    case AnyRef =>
  }

}

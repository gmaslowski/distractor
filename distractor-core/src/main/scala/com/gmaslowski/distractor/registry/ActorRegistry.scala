package com.gmaslowski.distractor.registry

import akka.actor.{Actor, ActorRef}
import com.gmaslowski.distractor.core.api.DistractorApi.{RegisterMsg, UnregisterMsg}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegisteredMsg, RegisteredMsg}

object ActorRegistry {

  case object GetRegisteredMsg

  case class RegisteredMsg(list: List[String])

}

trait ActorRegistry extends Actor {

  val registry = collection.mutable.HashMap[String, ActorRef]()

  def handleRegistry: Receive = {
    case RegisterMsg(id, toRegister) =>
      registry += id -> toRegister
    case UnregisterMsg(id) =>
      registry -= id
    case GetRegisteredMsg =>
      sender() ! new RegisteredMsg(registry.keys.toList)
  }

}

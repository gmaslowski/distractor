package com.fp.distractor.registry

import akka.actor.{Actor, ActorRef}
import com.fp.distractor.registry.ActorRegistry.{RegisteredMsg, GetRegisteredMsg, UnregisterMsg, RegisterMsg}

object ActorRegistry {

  case class RegisterMsg(id: String, toRegister: ActorRef)
  case class UnregisterMsg(id: String)
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

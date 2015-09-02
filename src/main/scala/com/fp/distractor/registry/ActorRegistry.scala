package com.fp.distractor.registry

import akka.actor.{Actor, ActorRef}
import com.fp.distractor.registry.ActorRegistry.{UnregisterMsg, RegisterMsg}

object ActorRegistry {

  case class RegisterMsg(id: String, toRegister: ActorRef)
  case class UnregisterMsg(id: String)

}

trait ActorRegistry extends Actor {

  val registry = collection.mutable.HashMap[String, ActorRef]()

  def handleRegistry: Receive = {
    case RegisterMsg(id, toRegister) =>
      registry += id -> toRegister
    case UnregisterMsg(id) =>
      registry -= id
  }

}

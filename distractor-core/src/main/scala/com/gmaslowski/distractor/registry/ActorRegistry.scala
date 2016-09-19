package com.gmaslowski.distractor.registry

import akka.actor.{Actor, ActorRef}
import com.gmaslowski.distractor.core.api.DistractorApi.{Register, Unregister}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegistered, RegisteredActors}

object ActorRegistry {

  case object GetRegistered
  case class RegisteredActors(registryName: String, list: List[String])
}

trait ActorRegistry extends Actor {

  val registry = collection.mutable.HashMap[String, ActorRef]()

  def registryName: String

  def handleRegistry: Receive = {
    case Register(id, toRegister) =>
      registry += id -> toRegister
    case Unregister(id) =>
      registry -= id
    case GetRegistered =>
      sender ! RegisteredActors(registryName, registry.keys.toList)
  }

}

package com.gmaslowski.distractor.registry

import java.util.Properties

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gmaslowski.distractor.core.api.DistractorApi.{Register, Unregister}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegistered, RegisteredActors}

object ActorRegistry {

  case object GetRegistered

  case class RegisteredActors(registryName: String, list: List[String])

  var properties: Properties = new Properties()

}

trait ActorRegistry extends Actor with ActorLogging {

  val registry = collection.mutable.HashMap[String, ActorRef]()

  def registryName: String

  def handleRegistry: Receive = {
    case Register(id, toRegister) =>
      log.info(s"Registering: $toRegister with id: $id")


      registry += id -> toRegister
    case Unregister(id) =>
      log.info(s"Unregistering id: $id")
      registry -= id
    case GetRegistered =>
      sender ! RegisteredActors(registryName, registry.keys.toList)
  }

}

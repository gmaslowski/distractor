package com.gmaslowski.distractor.core.transport

import akka.actor.{Actor, ActorLogging, Props}
import com.gmaslowski.distractor.core.api.DistractorApi.DistractorRequest
import com.gmaslowski.distractor.core.transport.TransportRegistry.SendBroadcast
import com.gmaslowski.distractor.registry.ActorRegistry

object TransportRegistry {
  def props = Props[TransportRegistry]

  case class SendBroadcast(broadcast: String)
}

class TransportRegistry extends Actor with ActorLogging with ActorRegistry {

  override def receive = handleRegistry orElse (handle)

  def handle: Receive = {
    case SendBroadcast(broadcast) =>
      registry.foreach(transportEntry => transportEntry._2 ! new DistractorRequest(broadcast))
  }

  override def registryName: String = "transports"
}
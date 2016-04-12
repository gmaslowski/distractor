package com.gmaslowski.distractor.core.transport

import akka.actor.{Actor, ActorLogging, Props}
import TransportRegistry.SendBroadcast
import com.gmaslowski.distractor.core.transport.api.{TransportApi, Message}
import TransportApi.Say
import com.gmaslowski.distractor.core.transport.api.Message
import com.gmaslowski.distractor.registry.ActorRegistry

object TransportRegistry {
  def props = Props[TransportRegistry]

  case class SendBroadcast(broadcast: Message)

}

class TransportRegistry extends Actor with ActorLogging with ActorRegistry {

  override def receive = handleRegistry orElse (handle)

  def handle: Receive = {
    case SendBroadcast(broadcast) =>
      registry.foreach(transportEntry => transportEntry._2 ! new Say(broadcast))
  }
}
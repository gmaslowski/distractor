package com.fp.distractor.core.transport

import akka.actor.{Actor, ActorLogging, Props}
import com.fp.distractor.core.transport.TransportRegistry.SendBroadcast
import com.fp.distractor.core.transport.api.TransportApi.{Message, Say}
import com.fp.distractor.registry.ActorRegistry

object TransportRegistry {
  def props = Props[TransportRegistry]

  case class SendBroadcast(broadcast: Message)

}

class TransportRegistry extends Actor with ActorLogging with ActorRegistry {

  override def receive = handleRegistry orElse(handle)

  def handle: Receive = {
    case SendBroadcast(broadcast) =>
      registry.foreach(transportEntry => transportEntry._2 ! new Say(broadcast))
  }
}
package com.fp.distractor.core.transport.skype

import akka.actor.{Actor, ActorLogging}
import com.fp.distractor.core.transport.api.TransportApi.Say

object SkypeTransportActor {

  val DEF = null

}

class SkypeTransportActor extends Actor with ActorLogging {

  import com.fp.distractor.core.transport.skype.SkypeTransportActor._

  override def preStart() {
    log.info(DEF)
  }

  override def receive = {
    case Say => 
  }
}

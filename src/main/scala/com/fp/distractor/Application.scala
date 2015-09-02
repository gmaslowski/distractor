package com.fp.distractor

import akka.actor.ActorSystem
import com.fp.distractor.core.Distractor
import com.fp.distractor.core.transport.telnet.TelnetTransportActor

object Application {

  val system = ActorSystem("distractor")
  system.actorOf(Distractor.props, "distractor")

  def main(args: Array[String]) {
    system.actorOf(TelnetTransportActor.props, "telnet")
  }

}

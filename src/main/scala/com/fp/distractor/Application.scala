package com.fp.distractor

import akka.actor.ActorSystem
import com.fp.distractor.core.{TransportManager, ReactorManager}

object Application {

  val system = ActorSystem("distractor")
  system.actorOf(ReactorManager.props)
  system.actorOf(TransportManager.props)

}

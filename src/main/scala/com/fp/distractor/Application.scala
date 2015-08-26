package com.fp.distractor

import akka.actor.ActorSystem
import com.fp.distractor.core.Distractor

object Application {

  val system = ActorSystem("distractor")
  system.actorOf(Distractor.props)

}

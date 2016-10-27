package com.gmaslowski.distractor.core.api

import akka.actor.ActorRef

object DistractorApi {

  case class DistractorRequest(command: String)
  case class DistractorResponse(message: String)

  case class Register(id: String, toRegister: ActorRef)
  case class Unregister(id: String)
}

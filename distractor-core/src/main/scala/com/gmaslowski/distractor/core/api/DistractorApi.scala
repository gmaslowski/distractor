package com.gmaslowski.distractor.core.api

object DistractorApi {

  case class DistractorRequest(commandMessage: String)
  case class DistractorResponse(message: String)

}

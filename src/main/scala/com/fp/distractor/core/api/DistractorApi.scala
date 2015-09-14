package com.fp.distractor.core.api

object DistractorApi {

  case class DistractorRequest(commandMessage: String)
  case class DistractorResponse(message: String)

}

package com.gmaslowski.distractor.core.reactor.api

object ReactorApi {

  case class ReactorRequest(reactorId: String, data: String, passThrough: String = null)
  case class ReactorResponse(reactorId: String, json: String, passThrough: String = null)
}

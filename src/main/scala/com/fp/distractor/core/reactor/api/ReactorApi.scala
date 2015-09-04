package com.fp.distractor.core.reactor.api

object ReactorApi {

  case class ReactorRequest(reactorId: String, data: String)

  case class ReactorResponse(reactorId: String, data: String)

}



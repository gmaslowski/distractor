package com.fp.distractor.core.transport.api

object TransportApi {

  class Message(val msg: String)

  case class Say(message: Message)
}

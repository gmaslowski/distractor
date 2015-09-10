package com.fp.distractor.core.transport.api

object Message {
  val REQUEST_INDICATOR: String = "/"

  def fromStringCommand(reactorId: String, command: String): Message = new Message(reactorId, command)
}

/**
 *
 * examples:
 * /info
 * /system ls -CFal
 * /system ls -CFal --broadcast
 * /jenkins jobs
 * /jenkins jobs --help
 *
 *
 * @param reactorId
 * @param command
 */
case class Message(reactorId: String, command: String)
package com.gmaslowski.distractor.transport.telnet

import akka.actor.{ActorRef, ActorSelection}
import akka.event.LoggingAdapter
import org.apache.mina.core.session.IoSession

class TelnetHandlerTest extends AbstractUnitTest {

  "TelnetHandler" should {
    "send a message to distractor on incoming telnet message" in {
      val distractor: ActorSelection = mock[ActorSelection]
      // given
      val telnetHandler = new TelnetHandler(mock[LoggingAdapter], distractor, mock[ActorRef])

      // when
      telnetHandler.messageReceived(any[IoSession], any())

      // then
      verify(distractor).tell(any(), any())
    }
  }

}

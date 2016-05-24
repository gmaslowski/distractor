package com.gmaslowski.distractor.transport.telnet

import akka.actor.{ActorRef, ActorSelection}
import akka.event.LoggingAdapter
import com.gmaslowski.distractor.test.common.AbstractUnitTest
import org.apache.mina.core.session.IoSession
import org.mockito.Matchers.any
import org.mockito.Mockito.verify

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

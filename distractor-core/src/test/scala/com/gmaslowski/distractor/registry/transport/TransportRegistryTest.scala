package com.gmaslowski.distractor.registry.transport

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.distractor.core.api.DistractorApi.{DistractorRequest, Register}
import com.gmaslowski.distractor.core.transport.TransportRegistry.{SendBroadcast, props}
import com.gmaslowski.distractor.test.common.AkkaActorTest

class TransportRegistryTest extends AkkaActorTest {

  val transportRegistry = TestActorRef(props)
  val registeredTransportId: String = "someTransportId"

  "ActorRegistry" should {
    "forward message to existing reactor" in {

      // given
      val reactor = TestProbe()
      transportRegistry ! Register(registeredTransportId, reactor.ref)

      // when
      transportRegistry ! SendBroadcast("someData")

      // then
      reactor.expectMsg(DistractorRequest("someData"))
    }
  }

}

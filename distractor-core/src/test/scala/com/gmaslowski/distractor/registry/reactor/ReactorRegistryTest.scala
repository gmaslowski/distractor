package com.gmaslowski.distractor.registry.reactor

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.ReactorRegistry.props
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.gmaslowski.distractor.test.common.AkkaActorTest

class ReactorRegistryTest extends AkkaActorTest {

  val reactorRegistry = TestActorRef(props)
  val registeredReactorId: String = "someReactorId"

  "ActorRegistry" should {
    "forward message to existing reactor" in {

      // given
      val reactor = TestProbe()
      reactorRegistry ! Register(registeredReactorId, reactor.ref)

      // when
      reactorRegistry ! ReactorRequest(registeredReactorId, "someData")

      // then
      reactor.expectMsg(ReactorRequest(registeredReactorId, "someData"))
    }
  }
}

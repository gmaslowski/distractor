package com.fp.distractor.reactor

import akka.testkit.{TestActorRef, TestProbe}
import com.fp.common.AkkaActorTest
import com.fp.distractor.core.reactor.ReactorRegistry.props
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.fp.distractor.registry.ActorRegistry.RegisterMsg

class ReactorRegistryTest extends AkkaActorTest {

  val reactorRegistry = TestActorRef(props)
  val registeredReactorId: String = "someReactorId"

  "ActorRegistry" should {
    "forward message to existing reactor" in {

      // given
      val reactor = TestProbe()
      reactorRegistry ! RegisterMsg(registeredReactorId, reactor.ref)

      // when
      reactorRegistry ! ReactorRequest(registeredReactorId, "someData")

      // then
      reactor.expectMsg(ReactorRequest(registeredReactorId, "someData"))
    }
  }
}

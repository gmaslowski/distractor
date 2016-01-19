package com.gmaslowski.distractor.reactor

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.common.AkkaActorTest
import com.gmaslowski.distractor.core.reactor.ReactorRegistry
import ReactorRegistry.props
import com.gmaslowski.distractor.core.reactor.api.ReactorApi
import ReactorApi.ReactorRequest
import com.gmaslowski.distractor.registry.ActorRegistry
import ActorRegistry.RegisterMsg

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

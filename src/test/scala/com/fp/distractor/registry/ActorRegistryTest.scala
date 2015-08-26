package com.fp.distractor.registry

import akka.actor.{Actor, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.fp.common.AkkaActorTest
import com.fp.distractor.registry.ActorRegistry.{RegisterMsg, UnregisterMsg}

class ActorRegistryTest extends AkkaActorTest {

  val ACTOR_ID: String = "some_id"

  "an actor with ActorRegistry trait mixed in" should {

    // given
    val actor = TestActorRef(Props[WithActorRegistry])
    val actorToRegister = TestProbe()

    "have the possibility to register other actors" in {

      // when
      actor ! RegisterMsg(ACTOR_ID, actorToRegister.ref)

      // then
      val withActorRegistry = actor.underlyingActor.asInstanceOf[WithActorRegistry]
      withActorRegistry.registry contains ACTOR_ID
      withActorRegistry.registry.get(ACTOR_ID).get eq actorToRegister.ref
    }

    "have the possibility to unregister other actors" in {

      // when
      actor ! RegisterMsg(ACTOR_ID, actorToRegister.ref)
      actor ! UnregisterMsg(ACTOR_ID)

      // then
      actor.underlyingActor.asInstanceOf[WithActorRegistry].registry.isEmpty
    }
  }

}

class WithActorRegistry extends Actor with ActorRegistry

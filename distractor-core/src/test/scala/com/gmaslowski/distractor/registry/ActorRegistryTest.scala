package com.gmaslowski.distractor.registry

import akka.actor.{Actor, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.distractor.core.api.DistractorApi.{Register, Unregister}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegistered, RegisteredActors}
import com.gmaslowski.distractor.test.common.AkkaActorTest

class ActorRegistryTest extends AkkaActorTest {

  val ACTOR_ID: String = "some_id"

  "an actor with ActorRegistry trait mixed in" should {

    // given
    val actorToRegister = TestProbe()
    val actor = TestActorRef(Props[WithActorRegistry])

    "have the possibility to register other actors" in {

      // when
      actor ! Register(ACTOR_ID, actorToRegister.ref)
      actor ! GetRegistered

      // then
      expectMsg(RegisteredActors("abstract", List(ACTOR_ID)))
    }

    "have the possibility to unregister other actors" in {

      // when
      actor ! Register(ACTOR_ID, actorToRegister.ref)
      actor ! Unregister(ACTOR_ID)
      actor ! GetRegistered

      // then
      expectMsg(RegisteredActors("abstract", List.empty[String]))
    }
  }

}

class WithActorRegistry extends Actor with ActorRegistry {
  override def receive = handleRegistry

  override def registryName: String = "abstract"
}

package com.fp.distractor.core

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.fp.common.AkkaActorTest
import com.fp.distractor.core.ReactorTransportMixer.React
import com.fp.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}

class ReactorTransportMixerTest extends AkkaActorTest {

  "ReactorMixer" should {

    // given
    val reactorRegistry = TestProbe()
    val transportRegistry = TestProbe()
    val actor = TestActorRef(Props(classOf[ReactorTransportMixer], reactorRegistry.ref, transportRegistry.ref))
    val sender = TestProbe()

    "forward the request to Reactor" in {

      // when
      actor.tell(new React("/system ls -CFal"), sender.ref)

      // then
      reactorRegistry.expectMsgClass(classOf[ReactorRequest])
    }
  }

  "parsing function" should {

    // given
    val msg = "/system ls -CFal"

    "parse and provide reactorId and command" in {

      // when
      val tuple: (String, String) = ReactorTransportMixer.extractReactorAndCommandFrom(msg)

      // then
      assert(tuple._1 equals "system")
      assert(tuple._2 equals "ls -CFal")
    }
  }
}

package com.fp.distractor.core

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.fp.common.AkkaActorTest
import com.fp.distractor.core.ReactorTransportMixer.React
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest

class ReactorTransportMixerTest extends AkkaActorTest {

  "ReactorMixer" should {

    // given
    val reactorRegistry = TestProbe()
    val actor = TestActorRef(Props(classOf[ReactorTransportMixer], reactorRegistry.ref))
    val sender = TestProbe()

    "forward the request to Reactor" in {

      // when
      actor.tell(new React("/system ls -CFal"), sender.ref)

      // then
      reactorRegistry.expectMsgClass(classOf[ReactorRequest])
    }
  }

  "parsing function" should {

    "parse and provide reactorId and command" in {

      // given
      val msg = "/system ls -CFal"

      // when
      val respone: (String, String) = ReactorTransportMixer.extractReactorAndCommandFrom(msg)

      // then
      assert(respone._1 equals "system")
      assert(respone._2 equals "ls -CFal")
    }

    "work for no-argument commands -> /info" in {

      // given
      val msg = "/info"

      // when
      val response: (String, String) = ReactorTransportMixer.extractReactorAndCommandFrom(msg)

      // then
      assert(response._1 equals "info")
      assert(response._2 equals "")
    }

    "not care on whitespaces between command and arguments" in {

      // given
      val msg = "/system   ls -CFal"

      // when
      val respone: (String, String) = ReactorTransportMixer.extractReactorAndCommandFrom(msg)

      // then
      assert(respone._1 equals "system")
      assert(respone._2 equals "ls -CFal")
    }
  }
}

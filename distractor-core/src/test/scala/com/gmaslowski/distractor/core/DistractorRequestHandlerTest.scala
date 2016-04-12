package com.gmaslowski.distractor.core

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.common.AkkaActorTest
import com.gmaslowski.distractor.core.api.{DistractorApi, DistractorRequestHandler}
import DistractorApi.DistractorRequest
import com.gmaslowski.distractor.core.api.DistractorRequestHandler
import com.gmaslowski.distractor.core.reactor.api.ReactorApi
import ReactorApi.ReactorRequest

class DistractorRequestHandlerTest extends AkkaActorTest {

  "DistractorRequestHandler" should {

    // given
    val reactorRegistry = TestProbe()
    val actor = TestActorRef(Props(classOf[DistractorRequestHandler], reactorRegistry.ref))
    val sender = TestProbe()

    "forward the request to Reactor" in {

      // when
      actor.tell(DistractorRequest("/system ls -CFal"), sender.ref)

      // then
      reactorRegistry.expectMsgClass(classOf[ReactorRequest])
    }
  }

  "parsing function" should {

    "parse and provide reactorId and command" in {

      // given
      val msg = "/system ls -CFal"

      // when
      val respone: (String, String) = DistractorRequestHandler.extractReactorAndCommandFrom(msg)

      // then
      assert(respone._1 equals "system")
      assert(respone._2 equals "ls -CFal")
    }

    "work for no-argument commands -> /info" in {

      // given
      val msg = "/info"

      // when
      val response: (String, String) = DistractorRequestHandler.extractReactorAndCommandFrom(msg)

      // then
      assert(response._1 equals "info")
      assert(response._2 equals "")
    }

    "not care on whitespaces between command and arguments" in {

      // given
      val msg = "/system   ls -CFal"

      // when
      val respone: (String, String) = DistractorRequestHandler.extractReactorAndCommandFrom(msg)

      // then
      assert(respone._1 equals "system")
      assert(respone._2 equals "ls -CFal")
    }
  }
}

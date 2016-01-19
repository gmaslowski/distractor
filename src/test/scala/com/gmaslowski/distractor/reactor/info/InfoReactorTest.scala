package com.gmaslowski.distractor.reactor.info

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.common.AkkaActorTest
import com.gmaslowski.distractor.core.reactor.api.ReactorApi
import ReactorApi.ReactorRequest
import com.gmaslowski.distractor.core.reactor.info.InfoReactor
import InfoReactor.Information
import com.gmaslowski.distractor.core.reactor.info.InfoReactor
import com.gmaslowski.distractor.core.transport.api.{TransportApi, Message}
import TransportApi.Say
import com.gmaslowski.distractor.registry.ActorRegistry
import ActorRegistry.{GetRegisteredMsg, RegisteredMsg}
import com.gmaslowski.distractor.core.transport.api.Message

class InfoReactorTest extends AkkaActorTest {

  val transportRegistry = TestProbe()
  val reactorRegistry = TestProbe()

  "InfoReactor" should {

    // given
    val expectedInformation: Information = Information("name", "version", "author")
    val infoReactor = TestActorRef.apply(InfoReactor.props(expectedInformation, transportRegistry.ref, reactorRegistry.ref))

    "respond with proper version information" in {

      // when
      infoReactor ! ReactorRequest("info", "")

      reactorRegistry.expectMsg(GetRegisteredMsg)
      transportRegistry.expectMsg(GetRegisteredMsg)
      reactorRegistry.reply(RegisteredMsg(List("info", "system")))
      transportRegistry.reply(RegisteredMsg(List("telnet", "skype")))

      // then
      expectMsg(new Say(new Message("info", s"$expectedInformation\ntransports or reactors:\ntelnet\nskype\n\ntransports or reactors:\ninfo\nsystem\n")))
    }
  }
}
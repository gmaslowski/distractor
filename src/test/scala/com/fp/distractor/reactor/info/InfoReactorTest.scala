package com.fp.distractor.reactor.info

import akka.testkit.{TestActorRef, TestProbe}
import com.fp.common.AkkaActorTest
import com.fp.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.fp.distractor.core.reactor.info.InfoReactor
import com.fp.distractor.core.reactor.info.InfoReactor.Information
import com.fp.distractor.core.transport.api.Message
import com.fp.distractor.core.transport.api.TransportApi.Say
import com.fp.distractor.registry.ActorRegistry.{GetRegisteredMsg, RegisteredMsg}

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
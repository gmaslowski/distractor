package com.gmaslowski.distractor.reactor.info

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.gmaslowski.distractor.registry.ActorRegistry
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegisteredMsg, RegisteredMsg}
import com.gmaslowski.distractor.test.common.AkkaActorTest
import com.gmaslowski.distractor.transport.info.InfoReactor
import com.gmaslowski.distractor.transport.info.InfoReactor.Information

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
      expectMsg(ReactorResponse("info", s"$expectedInformation\ntransports or reactors:\ntelnet\nskype\n\ntransports or reactors:\ninfo\nsystem\n"))
    }
  }
}
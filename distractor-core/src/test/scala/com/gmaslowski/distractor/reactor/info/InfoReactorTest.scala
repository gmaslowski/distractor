package com.gmaslowski.distractor.reactor.info

import akka.testkit.{TestActorRef, TestProbe}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.gmaslowski.distractor.registry.ActorRegistry.{GetRegistered, RegisteredActors}
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

      reactorRegistry.expectMsg(GetRegistered)
      transportRegistry.expectMsg(GetRegistered)
      reactorRegistry.reply(RegisteredActors("reactors", List("info", "system")))
      transportRegistry.reply(RegisteredActors("transports", List("telnet", "skype")))

      // then
      expectMsg(ReactorResponse("info", "{\"appName\":\"name\",\"author\":\"author\",\"transports\":[\"telnet\",\"skype\"],\"reactors\":[\"info\",\"system\"],\"version\":\"version\"}"))
    }
  }
}
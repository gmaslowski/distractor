package com.gmaslowski.distractor.reactor.jira

import akka.testkit.TestActorRef
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.gmaslowski.distractor.test.common.AkkaActorTest
import org.mockito.Matchers.anyString
import org.mockito.Mockito.verify
import org.scalatest.mock.MockitoSugar
import play.api.libs.ws.ahc.AhcWSClient

class JiraReactorTest extends AkkaActorTest with MockitoSugar {

  val ahcWsClient = mock[AhcWSClient]

  "JiraReactor" should {

    // given
    val jira = TestActorRef.apply(JiraReactor.props(ahcWsClient))

    "invoke wsClient" in {
      // when
      jira ! ReactorRequest("jira", "PROJ-12")

      // then
      verify(ahcWsClient).url(anyString())
    }
  }
}

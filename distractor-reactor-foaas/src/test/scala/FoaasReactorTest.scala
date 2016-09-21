package com.gmaslowski.distractor.reactor.foaas

import akka.testkit.TestActorRef
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorRequest
import com.gmaslowski.distractor.test.common.AkkaActorTest
import org.mockito.Matchers.anyString
import org.mockito.Mockito.verify
import org.scalatest.mock.MockitoSugar
import play.api.libs.ws.ahc.AhcWSClient

class FoaasReactorTest extends AkkaActorTest with MockitoSugar {

  val ahcWsClient = mock[AhcWSClient]

  "FoaasReactor" should {

    // given
    val foaas = TestActorRef.apply(FoaasReactor.props(ahcWsClient))

    "invoke wsClient" in {
      // when
      foaas ! ReactorRequest("foaas", "anyString")

      // then
      verify(ahcWsClient).url(anyString())
    }
  }
}

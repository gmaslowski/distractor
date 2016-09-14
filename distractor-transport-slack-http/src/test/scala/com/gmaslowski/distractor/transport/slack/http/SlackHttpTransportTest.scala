package com.gmaslowski.distractor.transport.slack.http

import com.gmaslowski.distractor.test.common.AkkaActorTest
import com.gmaslowski.distractor.transport.slack.http.SlackHttpTransport.makeDistractorCommand

class SlackHttpTransportTest extends AkkaActorTest {

  "slackCommand function" should {

    "provide distractor command" in {

      // given
      val slackCommand = "one=two&two=three&text=1+2+3"

      // when
      val respone = makeDistractorCommand(slackCommand)

      // then
      assert(respone equals "/1 2 3")
    }
  }
}

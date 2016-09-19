package com.gmaslowski.distractor.transport.slack.http

import com.gmaslowski.distractor.test.common.AkkaActorTest
import com.gmaslowski.distractor.transport.slack.http.SlackHttpTransport.makeDistractorCommand

class SlackHttpTransportTest extends AkkaActorTest {

  "slackCommand function" should {

    "provide distractor command" in {

      // given
      val slackCommand = "one=two&two=three&text=1+2+3"

      // when
      val response = makeDistractorCommand(slackCommand)

      // then
      assert(response equals "/1 2 3")
    }

    "provide distractor command with decoded url parts" in {

      // given
      val slackCommand = "text=springboot+ipmp-test+metrics%2Fmem.free"

      // when
      val response = makeDistractorCommand(slackCommand)

      // then
      println(response)
      assert(response equals "/springboot ipmp-test metrics/mem.free")
    }
  }
}

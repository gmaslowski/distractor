package com.gmaslowski.distractor.reactor.jira

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.{AhcConfigBuilder, AhcWSClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object JiraReactor {

  def props = Props[JiraReactor]
}

class JiraReactor extends Actor with ActorLogging {


  override def receive = {
    case reactorRequest: ReactorRequest =>
      implicit val mat = ActorMaterializer.apply(ActorMaterializerSettings.create(context.system))

      val issueNumber = reactorRequest.data

      val client = new AhcWSClient(new AhcConfigBuilder().build())

      val responseFuture: Future[WSResponse] =
        client
          .url(s"${sys.env("JIRA_LINK")}/rest/api/2/issue/$issueNumber")
          .withAuth(sys.env("JIRA_USER"), sys.env("JIRA_PASS"), BASIC)
          .get()

      val result = Await.result(responseFuture, Duration.Inf)

      client.close()

      context.sender() forward ReactorResponse(reactorRequest.reactorId, result.body)
  }
}

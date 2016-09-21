package com.gmaslowski.distractor.reactor.jira

import akka.actor.{Actor, ActorLogging, Props}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.ws.ahc.AhcWSClient

object JiraReactor {

  def props(ahcWsClient: AhcWSClient) = Props(classOf[JiraReactor], ahcWsClient)

}

class JiraReactor(val ahcWsClient: AhcWSClient) extends Actor with ActorLogging {
  implicit val ec = context.dispatcher

  val issueCommand = "([A-Z]+-[0-9]+)".r
  val jqlCommand = "jql=(.+)".r

  override def receive = {

    case reactorRequest: ReactorRequest =>

      val sender = context.sender()

      val url = reactorRequest.data match {
        case issueCommand(issueNumber) =>
          s"${sys.env("JIRA_LINK")}/rest/api/2/issue/$issueNumber?fields=key,summary"

        case jqlCommand(jql) =>
          s"${sys.env("JIRA_LINK")}/rest/api/2/search?fields=key,summary&jql=$jql"
      }

      issueJiraRequest(url)
        .onSuccess {
          case result =>
            sender forward ReactorResponse(reactorRequest.reactorId, result.body)
        }
  }

  def issueJiraRequest(url: String) =
    ahcWsClient
      .url(url)
      .withAuth(sys.env("JIRA_USER"), sys.env("JIRA_PASS"), BASIC)
      .get()
}

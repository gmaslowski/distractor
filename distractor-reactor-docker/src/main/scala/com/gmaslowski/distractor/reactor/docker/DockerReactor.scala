package com.gmaslowski.distractor.reactor.docker

import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props}
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.typesafe.config.ConfigFactory.load
import org.slf4j.{Logger, LoggerFactory}

object DockerReactor {

  val log: Logger = LoggerFactory.getLogger(classOf[DockerReactor])

  def startDockerReactor(actorSystem: ActorSystem = ActorSystem("docker-reactor-system", load("docker-reactor-system.conf"))) = {
    val reactorRegistry: ActorSelection = actorSystem.actorSelection("akka.tcp://distractor@127.0.0.1:2552/user/reactor-registry")

    val client = DockerClientBuilder.getInstance().build()

    reactorRegistry ! Register("docker", actorSystem.actorOf(DockerReactor.props(client)))
  }

  private def props(client: DockerClient) = Props(classOf[DockerReactor], client)
}

class DockerReactor(client: DockerClient) extends Actor with ActorLogging {

  val listImagesCommand = "(images)".r
  val listContainersCommand = "(ps)".r
  val inspectContainersCommand = "(inspect) ([a-zA-Z0-9]+)".r

  override def receive = {

    case ReactorRequest(reactorId, data, passThrough) =>
      val response = data match {

        case listImagesCommand(images) =>
          client.listImagesCmd().exec()

        case listContainersCommand(ps) =>
          client.listContainersCmd().exec()

        case inspectContainersCommand(inspect, container) =>
          client.inspectContainerCmd(container).exec()

      }

      context.sender() forward ReactorResponse(reactorId, response.toString)
  }
}

package com.gmaslowski.distractor.reactor.docker

import akka.actor.{Actor, ActorLogging, Props}
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import org.slf4j.{Logger, LoggerFactory}

object DockerReactor {

  val log: Logger = LoggerFactory.getLogger(classOf[DockerReactor])

  def props = {
    val client = DockerClientBuilder.getInstance().build()
    Props(classOf[DockerReactor], client)
  }
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

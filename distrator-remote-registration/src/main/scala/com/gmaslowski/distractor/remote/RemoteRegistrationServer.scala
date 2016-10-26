package com.gmaslowski.distractor.remote

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import com.gmaslowski.distractor.remote.RemoteRegistrationServer.UseReactorRegistry

import scala.language.postfixOps

object RemoteRegistrationServer {
  def props = Props[RemoteRegistrationServer]

  case class UseReactorRegistry(reactorRegistry: ActorRef)
}

class RemoteRegistrationServer extends Actor with ActorLogging {
  val tcpPort = 8082
  implicit val sys = context system

  override def receive: Receive = {
    case UseReactorRegistry(reactorRegistry) =>
      context become bindAndHandle(reactorRegistry)
  }

  def bindAndHandle(reactorRegistry: ActorRef): Receive = {
    tcp ! Bind(self, new InetSocketAddress(tcpPort))

    {
      case Bound(localAddress) =>
        log.info(s"Started remote reactor registration TCP API at port ${localAddress.getPort}")

      case CommandFailed(_: Bind) =>
        // TODO: consider fallback ports, retrying strategy etc.
        log.error(s"Could not bind on port $tcpPort, stopping")
        context stop self

      case Connected(remote, _) =>
        log.info(s"Accepted a connection from $remote")

        sender() ! Register(handler(reactorRegistry))
    }
  }

  // TODO: Find a better way to make this testable
  def handler(reactorRegistry: ActorRef) = context.actorOf(RemoteReactorProxy.props(reactorRegistry))

  // TODO: Consider using cake pattern to inject the IO(Tcp)
  def tcp = IO(Tcp)
}

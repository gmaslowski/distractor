package com.gmaslowski.distractor.core

import akka.actor._
import akka.kernel.Bootable
import com.gmaslowski.distractor.core.api.DistractorRequestHandler
import com.gmaslowski.distractor.core.reactor.ReactorRegistry
import com.gmaslowski.distractor.core.reactor.info.InfoReactor
import com.gmaslowski.distractor.core.reactor.info.InfoReactor.Information
import com.gmaslowski.distractor.core.reactor.system.SystemReactor
import com.gmaslowski.distractor.core.transport.TransportRegistry
import com.gmaslowski.distractor.registry.ActorRegistry.RegisterMsg
import com.gmaslowski.distractor.transport.telnet.TelnetTransport

class DistractorKernel extends Bootable {

  val system = ActorSystem("distractor")

  def startup = {
    system.actorOf(Distractor.props, "distractor")

    // fixme: transport should be distractor-kernel independent
    system.actorOf(TelnetTransport.props, "telnet")
  }

  def shutdown = {
    system.shutdown()
  }
}

object Distractor {
  def props = Props[Distractor]
}

class Distractor extends Actor with ActorLogging {

  var transportRegistry: ActorRef = context.system.deadLetters
  var reactorRegistry: ActorRef = context.system.deadLetters
  var requestHandler: ActorRef = context.system.deadLetters

  def receive = {
    case msg: AnyRef => unhandled(msg)
  }

  override def preStart() = {
    transportRegistry = context.actorOf(TransportRegistry.props, "transport-registry")
    reactorRegistry = context.actorOf(ReactorRegistry.props, "reactor-registry")
    requestHandler = context.actorOf(DistractorRequestHandler.props(reactorRegistry), "request-handler")

    createAndRegisterInfoReactor
    reactorRegistry ! RegisterMsg("system", context.actorOf(SystemReactor.props))
  }

  def createAndRegisterInfoReactor: Unit = {

    val information: Information = new Information(
      context.system.settings.config.getString("reactor.info.appName"),
      context.system.settings.config.getString("reactor.info.version"),
      context.system.settings.config.getString("reactor.info.author")
    )

    reactorRegistry ! RegisterMsg("info", context.actorOf(InfoReactor.props(information, transportRegistry, reactorRegistry)))
  }
}

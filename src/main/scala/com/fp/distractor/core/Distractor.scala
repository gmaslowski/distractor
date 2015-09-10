package com.fp.distractor.core

import akka.actor._
import akka.kernel.Bootable
import com.fp.distractor.core.reactor.ReactorRegistry
import com.fp.distractor.core.reactor.info.InfoReactor
import com.fp.distractor.core.reactor.info.InfoReactor.Information
import com.fp.distractor.core.reactor.system.SystemReactor
import com.fp.distractor.core.transport.TransportRegistry
import com.fp.distractor.core.transport.telnet.TelnetTransportActor
import com.fp.distractor.registry.ActorRegistry.RegisterMsg

class DistractorKernel extends Bootable {

  val system = ActorSystem("distractor")

  def startup = {
    system.actorOf(Distractor.props, "distractor")

    // fixme: transport should be distractor-kernel independent
    system.actorOf(TelnetTransportActor.props, "telnet")
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
  var mixer: ActorRef = context.system.deadLetters

  def receive = {
    case AnyRef =>
  }

  override def preStart() = {
    transportRegistry = context.actorOf(TransportRegistry.props, "transport-registry")
    reactorRegistry = context.actorOf(ReactorRegistry.props, "reactor-registry")
    mixer = context.actorOf(ReactorTransportMixer.props(reactorRegistry), "reactor-transport-mixer")

    createAndRegisterInfoReactor
    reactorRegistry ! RegisterMsg("system", context.actorOf(SystemReactor.props))
  }

  def createAndRegisterInfoReactor: Unit = {

    val information: Information = new Information(
      context.system.settings.config.getString("reactor.info.appName"),
      context.system.settings.config.getString("reactor.info.version"),
      context.system.settings.config.getString("reactor.info.author")
    )

    // fixme: the reactor registry was not in place yet ;(
    reactorRegistry ! RegisterMsg("info", context.actorOf(InfoReactor.props(information, transportRegistry, reactorRegistry)))
  }
}

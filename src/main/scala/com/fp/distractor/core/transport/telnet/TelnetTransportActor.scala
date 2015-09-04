package com.fp.distractor.core.transport.telnet

import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, Props}
import com.fp.distractor.core.transport.api.TransportApi.Say
import org.apache.mina.core.session.IdleStatus.BOTH_IDLE
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.transport.socket.SocketSessionConfig
import org.apache.mina.transport.socket.nio.NioSocketAcceptor

import scala.concurrent.duration.FiniteDuration

object TelnetTransportActor {
  val TELNET_PORT: Int = 8111
  val ONE_SECOND: FiniteDuration = FiniteDuration.apply(1, SECONDS)

  def props = Props[TelnetTransportActor]
}

class TelnetTransportActor extends Actor with ActorLogging {

  import com.fp.distractor.core.transport.telnet.TelnetTransportActor._

  var acceptor: NioSocketAcceptor = new NioSocketAcceptor

  override def preStart() {
    implicit val ec = context.dispatcher
    log.debug("Starting telnet message transport actor on port {}.", TELNET_PORT)

    acceptor.getFilterChain().addLast("logger", new LoggingFilter())
    acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))))

    configureSession(acceptor.getSessionConfig)

    // todo: learn the new way of getting actors by path.. since actorFor is deprecated
    acceptor.setHandler(new TelnetHandler(log, context.actorSelection("akka://distractor/user/distractor/reactor-transport-mixer"), self))

    acceptor.bind(new InetSocketAddress(TELNET_PORT))
  }

  private def configureSession(sessionConfig: SocketSessionConfig) {
    sessionConfig.setReadBufferSize(2048)
    sessionConfig.setIdleTime(BOTH_IDLE, 10)
  }

  override def postStop() {
    acceptor.dispose()
    acceptor.unbind()
    acceptor = null
  }

  override def receive: Receive = {
    case Say(message) =>
      val sessions = acceptor.getManagedSessions

    // todo: write response
  }
}
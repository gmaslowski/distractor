package com.gmaslowski.distractor.transport.telnet

import java.lang.Long
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.ReactorResponse
import com.gmaslowski.distractor.transport.telnet.TelnetTransport.TELNET_PORT
import org.apache.mina.core.session.IdleStatus.BOTH_IDLE
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.transport.socket.SocketSessionConfig
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import spray.json.JsonParser

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object TelnetTransport {
  val TELNET_PORT: Int = 8111
  val ONE_SECOND: FiniteDuration = FiniteDuration.apply(1, SECONDS)

  def props(transportRegistry: ActorRef) = Props(classOf[TelnetTransport], transportRegistry)
}

class TelnetTransport(transportRegistry: ActorRef) extends Actor with ActorLogging {

  var acceptor: NioSocketAcceptor = new NioSocketAcceptor

  override def preStart() {
    implicit val ec = context.dispatcher
    log.debug("Starting telnet message transport actor on port {}.", TELNET_PORT)

    acceptor.getFilterChain().addLast("logger", new LoggingFilter())
    acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))))

    configureSession(acceptor.getSessionConfig)

    transportRegistry ! Register("telnet", self)
    acceptor.setHandler(new TelnetHandler(log, context.actorSelection("akka://distractor/user/distractor/request-handler"), self))
    acceptor.setReuseAddress(true)

    // fixme: use future for that; or a spawned actor
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

    acceptor.getManagedSessions.asScala.foreach(entry => entry._2.close(true))
  }

  override def receive: Receive = {
    case ReactorResponse(reactorId, message, passThrough) =>
      val sessions: mutable.Map[Long, IoSession] = acceptor.getManagedSessions.asScala.seq

      // todo: write response
      sessions.foreach(entry => entry._2.write(prettyJson(message)))
  }

  def prettyJson(json: String): String = {
    try {
      JsonParser(json).prettyPrint
    } catch {
      case exc: Exception => json
    }
  }
}

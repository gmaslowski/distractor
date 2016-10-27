package com.gmaslowski.distractor.remote

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}
import akka.io.Tcp.{Bind, Connected}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.gmaslowski.distractor.remote.RemoteRegistrationServer.UseReactorRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps

class RemoteRegistrationServerSpec extends TestKit(ActorSystem("RemoteRegistrationServerSpec"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A RemoteRegistrationServer actor" must {
    "bind to port 8082 after receiving initializing message" in {
      val tcpProbe = TestProbe()
      val server = TestActorRef(new RemoteRegistrationServer {
        override def tcp = tcpProbe.ref
      })

      val probe = TestProbe()
      server ! UseReactorRegistry(probe.ref)

      tcpProbe.expectMsg(Bind(server, new InetSocketAddress(8082)))
    }
  }

  // TODO: this is ugly, find a better way
  "An initialized RemoteRegistrationServer actor" must {
    "create a handler with proper registry on Connected message" in {
      // given
      val tcpProbe = TestProbe()
      val registryProbe = TestProbe()
      val anyProbe = TestProbe()
      val server = TestActorRef(new RemoteRegistrationServer {
        override def tcp = tcpProbe.ref
        override def handler(reactorRegistry: ActorRef) = {
          // then
          reactorRegistry shouldEqual registryProbe.ref
          anyProbe.ref
        }
      })

      // when
      server ! UseReactorRegistry(registryProbe.ref)
      server ! Connected(null, null)
    }
  }
}

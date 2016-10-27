package com.gmaslowski.distractor.remote

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill}
import akka.io.Tcp.{Received, Write}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.ByteString
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.gmaslowski.distractor.remote.RemoteReactorProxy.CorrelationId
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class RemoteReactorProxySpec extends TestKit(ActorSystem("RemoteReactorProxySpec"))
with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A RemoteReactorProxy actor" must {
    "register itself as a reactor after receiving initial TCP message" in {
      // given
      val registryProbe = TestProbe()
      val proxy = TestActorRef(new RemoteReactorProxy(registryProbe.ref))

      // when
      proxy ! Received(ByteString.fromString("name: reactor123\n", UTF_8.toString))

      // then
      registryProbe.expectMsg(Register("reactor123", proxy))
      proxy ! PoisonPill
    }
  }

  "A registered RemoteReactorProxy" must {
    "push a msg with correlationId upon ReactorRequest" in {
      // given
      val (proxy, _, connectionProbe) = getRegisteredProxyAndProbes()

      // when
      proxy ! ReactorRequest("reactor123", "data1 data2")

      // then
      val msg = connectionProbe.expectMsgType[Write]
      val verify = "^correlationId: \\w{8}(?:-\\w{4}){3}-\\w{12}, data: data1 data2\\n$".r.pattern.asPredicate()
      verify.test(msg.data.decodeString(UTF_8)) shouldEqual true
      proxy ! PoisonPill
    }

    "respond to the requester" in {
      // given
      val uuid = UUID.fromString("381c0ef7-d2f7-4ccb-aff3-72d1cba14851")
      val (proxy, _, connectionProbe) = getRegisteredProxyAndProbes(uuids = List(uuid))

      // when
      proxy ! ReactorRequest("reactor123", "data1 data2")
      connectionProbe.receiveOne(5 seconds)
      connectionProbe.reply(Received(ByteString("correlationId: 381c0ef7-d2f7-4ccb-aff3-72d1cba14851, data: data3 data4\n", UTF_8.toString)))

      // then
      expectMsg(ReactorResponse("reactor123", "data3 data4"))
      proxy ! PoisonPill
    }

    "respond with a timeout message if no response received" in {
      val (proxy, _, connectionProbe) = getRegisteredProxyAndProbes(timeoutDuration = Some(2 seconds))

      // when
      proxy ! ReactorRequest("reactor123", "data1 data2")

      // then
      expectMsg(5 seconds, ReactorResponse("reactor123", "{\"message\": \"Timed out, no response available.\"}"))
      proxy ! PoisonPill
    }

    "NOT respond when already timed out" in {
      val uuid = UUID.fromString("381c0ef7-d2f7-4ccb-aff3-72d1cba14851")
      val (proxy, _, connectionProbe) = getRegisteredProxyAndProbes(uuids = List(uuid), timeoutDuration = Some(2 seconds))

      // when
      proxy ! ReactorRequest("reactor123", "data1 data2")
      receiveOne(5 seconds)
      proxy ! Received(ByteString("correlationId: 381c0ef7-d2f7-4ccb-aff3-72d1cba14851, data: data3 data4\n", UTF_8.toString))

      // then
      expectNoMsg(5 seconds)
      proxy ! PoisonPill
    }

    "should respond to proper requesters (in case of out-of-order delivery)" in {
      // given
      val uuids = List(
        UUID.fromString("381c0ef7-d2f7-4ccb-aff3-72d1cba14851"),
        UUID.fromString("8bc80608-56f1-48ce-a3ac-7c99ab44422e")
      )
      val (proxy, _, connectionProbe) = getRegisteredProxyAndProbes(uuids = uuids)

      // when
      val (requesterA, requesterB) = (TestProbe(), TestProbe())
      proxy tell(ReactorRequest("reactor123", "data1 data2", "ptA"), requesterA.ref)
      proxy tell(ReactorRequest("reactor123", "data3 data4", "ptB"), requesterB.ref)
      connectionProbe.receiveOne(5 seconds)
      connectionProbe.reply(Received(ByteString("correlationId: 8bc80608-56f1-48ce-a3ac-7c99ab44422e, data: B\n", UTF_8.toString)))
      connectionProbe.receiveOne(5 seconds)
      connectionProbe.reply(Received(ByteString("correlationId: 381c0ef7-d2f7-4ccb-aff3-72d1cba14851, data: A\n", UTF_8.toString)))

      // then
      requesterA.expectMsg(ReactorResponse("reactor123", "A", "ptA"))
      requesterB.expectMsg(ReactorResponse("reactor123", "B", "ptB"))
      proxy ! PoisonPill
    }
  }

  def getRegisteredProxyAndProbes(uuids: List[UUID] = Nil, timeoutDuration: Option[FiniteDuration] = None) = {
    val registryProbe = TestProbe()
    val proxy = TestActorRef(new RemoteReactorProxy(registryProbe.ref) {
      var uuidList = uuids

      override def correlationId: CorrelationId = {
        uuidList match {
          case u :: us =>
            uuidList = us
            u
          case Nil =>
            super.correlationId
        }
      }

      override def timeout: FiniteDuration = timeoutDuration.getOrElse(super.timeout)
    })

    val connectionProbe = TestProbe()
    proxy tell(
      Received(ByteString.fromString("name: reactor123\n", UTF_8.toString)),
      connectionProbe.ref)

    (proxy, registryProbe, connectionProbe)
  }
}

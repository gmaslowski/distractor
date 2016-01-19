package com.gmaslowski.distractor.transport.skype

import akka.actor.{Props, Actor}
import com.fp.distractor.registry.ActorRegistry.RegisterMsg
import com.skype.{ApplicationAdapter, Skype, Stream}

object SkypeTransport {
  def props = Props[SkypeTransport]
}

class SkypeTransport extends Actor {

  override def preStart(): Unit = {
    context.actorSelection("akka://distractor/user/distractor/transport-registry") ! RegisterMsg("skype", self)


//    var application = Skype.addApplication("skype-transport")
//
//    application.addApplicationListener(new ApplicationAdapter() {
//
//      override def connected(stream: Stream) = {
//        printApplicationAndStreamName("connected:", stream)
//      }
//
//      override def disconnected(stream: Stream) = {
//        printApplicationAndStreamName("disconnected:", stream)
//      }
//
//      def printApplicationAndStreamName(header: String, stream: Stream) = {
//        println(header + stream.getApplication().getName() + "-" + stream.getId())
//      }
//    })
//
//    application.connectToAll()
//    Skype.chat()
  }

  override def receive: Receive = {
    case any: AnyRef =>
  }
}

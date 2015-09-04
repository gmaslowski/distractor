package com.fp.distractor.core.transport.telnet;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.LoggingAdapter;
import com.fp.distractor.core.ReactorTransportMixer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class TelnetHandler extends IoHandlerAdapter {

    private final LoggingAdapter log;
    private final ActorSelection mixxer;
    private final ActorRef telnetTransport;

    public TelnetHandler(LoggingAdapter log, ActorSelection mixxer, ActorRef telnetTransport) {
        this.log = log;
        this.mixxer = mixxer;
        this.telnetTransport = telnetTransport;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        mixxer.tell(new ReactorTransportMixer.React((String) message), telnetTransport);
    }
}
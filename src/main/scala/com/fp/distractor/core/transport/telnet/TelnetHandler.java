package com.fp.distractor.core.transport.telnet;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.LoggingAdapter;
import com.fp.distractor.core.api.DistractorApi;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class TelnetHandler extends IoHandlerAdapter {

    private final LoggingAdapter log;
    private final ActorSelection distractor;
    private final ActorRef telnetTransport;

    public TelnetHandler(LoggingAdapter log, ActorSelection distractor, ActorRef telnetTransport) {
        this.log = log;
        this.distractor = distractor;
        this.telnetTransport = telnetTransport;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        distractor.tell(new DistractorApi.DistractorRequest((String) message), telnetTransport);
    }
}
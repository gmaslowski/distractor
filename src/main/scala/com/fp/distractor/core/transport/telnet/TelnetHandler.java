package com.fp.distractor.core.transport.telnet;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.LoggingAdapter;
import com.fp.distractor.core.reactor.api.ReactorApi;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class TelnetHandler extends IoHandlerAdapter {

    private final LoggingAdapter log;
    private final ActorSelection reactorRegistry;
    private final ActorRef telnetTransport;

    public TelnetHandler(LoggingAdapter log, ActorSelection reactorRegistry, ActorRef telnetTransport) {
        this.log = log;
        this.reactorRegistry = reactorRegistry;
        this.telnetTransport = telnetTransport;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // todo: make it generic; reactor should be choosen upon the incoming message
        reactorRegistry.tell(new ReactorApi.ReactorRequest("info", (String) message), telnetTransport);
    }
}
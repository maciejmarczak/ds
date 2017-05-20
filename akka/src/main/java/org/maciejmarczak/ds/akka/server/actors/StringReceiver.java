package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;

abstract class StringReceiver extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::process)
                .matchAny(o -> System.out.println("unknown message " + o))
                .build();
    }

    abstract void process(String message);
}

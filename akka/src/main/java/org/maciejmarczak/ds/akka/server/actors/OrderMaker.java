package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;

class OrderMaker extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> System.out.println("order: " + s))
                .matchAny(o -> System.out.println("unknown message " + o))
                .build();
    }
}

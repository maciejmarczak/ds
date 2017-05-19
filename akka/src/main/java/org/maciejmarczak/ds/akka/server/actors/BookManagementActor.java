package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;

public class BookManagementActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, System.out::println)
                .matchAny(o -> System.out.println("unknown message " + o))
                .build();
    }
}

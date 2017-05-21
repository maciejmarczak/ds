package org.maciejmarczak.ds.akka.client.actors;

import akka.actor.AbstractActor;

public class ResponseReceiver extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, System.out::println)
                .match(byte[].class, bytes -> System.out.println(new String(bytes)))
                .build();
    }
}

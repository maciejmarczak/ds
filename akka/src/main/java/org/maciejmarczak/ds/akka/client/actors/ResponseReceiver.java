package org.maciejmarczak.ds.akka.client.actors;

import akka.actor.AbstractActor;
import org.maciejmarczak.ds.akka.model.Book;

public class ResponseReceiver extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Book.class, System.out::println)
                .build();
    }
}

package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;
import org.maciejmarczak.ds.akka.server.db.BookService;

abstract class StringReceiver extends AbstractActor {

    final BookService bookService =
            BookService.getInstance();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::process)
                .matchAny(o -> System.out.println("unknown message " + o))
                .build();
    }

    abstract void process(String message);
}

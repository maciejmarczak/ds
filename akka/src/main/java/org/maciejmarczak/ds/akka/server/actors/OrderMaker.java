package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.ActorSelection;
import org.maciejmarczak.ds.akka.model.BookNotFoundException;
import org.maciejmarczak.ds.akka.server.db.BookService;

class OrderMaker extends StringReceiver {

    private final BookService bookService =
            BookService.getInstance();

    @Override
    void process(String message) {
        ActorSelection actor = getContext().actorSelection(getSender().path());
        try {
            bookService.order(message);
        } catch (BookNotFoundException e) {
            // exception should be logged
            actor.tell(e.getMessage(), null);
            throw e;
        }
        actor.tell("'" + message + "' ordered successfully", null);
    }
}

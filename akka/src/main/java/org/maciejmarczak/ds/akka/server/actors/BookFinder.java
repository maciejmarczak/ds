package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.ActorSelection;
import org.maciejmarczak.ds.akka.model.Book;

class BookFinder extends StringReceiver {

    @Override
    void process(String message) {
        ActorSelection actor = getContext().actorSelection(getSender().path());
        Book book = bookService.findBook(message);

        String response = book != null ? book.toString() :
                "Book '" + message + "' could not be found";

        actor.tell(response, null);
    }
}

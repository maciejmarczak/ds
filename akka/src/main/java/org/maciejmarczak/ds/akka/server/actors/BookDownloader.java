package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.ActorSelection;
import org.maciejmarczak.ds.akka.model.Book;

class BookDownloader extends StringReceiver {

    @Override
    void process(String message) {
        ActorSelection actor = getContext().actorSelection(getSender().path());
        Book book = bookService.findBook(message);

        if (book == null) {
            actor.tell("Book '" + message + "' doesn't exist", null);
            return;
        }

        actor.tell(book.getContent(), null);
    }
}

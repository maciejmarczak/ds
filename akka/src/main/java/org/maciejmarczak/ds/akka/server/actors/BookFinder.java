package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;
import org.maciejmarczak.ds.akka.model.Book;
import org.maciejmarczak.ds.akka.server.db.BookService;

public class BookFinder extends AbstractActor {

    private final BookService bookService = BookService.getInstance();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::findBook)
                .build();
    }

    private void findBook(String title) {
        Book book = bookService.findBook(title);
        getContext().actorSelection(sender().path()).tell(book, null);
    }
}

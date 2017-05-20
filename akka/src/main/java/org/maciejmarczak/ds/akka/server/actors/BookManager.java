package org.maciejmarczak.ds.akka.server.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.maciejmarczak.ds.akka.model.BookRequest;

public class BookManager extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookRequest.class, this::dispatchJob)
                .matchAny(o -> System.out.println("unknown message " + o))
                .build();
    }

    private void dispatchJob(BookRequest bookRequest) {
        ActorRef sender = sender();
        String bookTitle = bookRequest.getBookTitle();

        switch (bookRequest.getType()) {
            case SEARCH: getChildFromContext("bookFinder").tell(bookTitle, sender); break;
            case ORDER: getChildFromContext("orderMaker").tell(bookTitle, sender); break;
            case DOWNLOAD: getChildFromContext("bookDownloader").tell(bookTitle, sender); break;
        }
    }

    private ActorRef getChildFromContext(String name) {
        return context().child(name).get();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(BookFinder.class), "bookFinder");
        context().actorOf(Props.create(OrderMaker.class), "orderMaker");
        context().actorOf(Props.create(BookDownloader.class), "bookDownloader");
    }
}

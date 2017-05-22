package org.maciejmarczak.ds.akka.server.actors;

import akka.NotUsed;
import akka.actor.ActorInterruptedException;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.maciejmarczak.ds.akka.model.Book;
import org.maciejmarczak.ds.akka.server.Server;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class BookDownloader extends StringReceiver {

    private final FiniteDuration resolveTime = Duration.create(20, TimeUnit.SECONDS);

    @Override
    void process(String message) {
        ActorSelection actor = getContext().actorSelection(getSender().path());
        Future<ActorRef> actorRefFuture = actor.resolveOne(resolveTime);

        Book book = bookService.findBook(message);

        if (book == null) {
            actor.tell("Book '" + message + "' doesn't exist", null);
            return;
        }

        String content = book.getContent();
        List<byte[]> chunks = new LinkedList<>();

        for (String sentence : content.split("\\.")) {
            chunks.add(sentence.getBytes());
        }

        Source<byte[], NotUsed> source = Source.from(chunks);

        Flow<byte[], byte[], NotUsed> flow = Flow.of(byte[].class).throttle(1, Duration.create(1,
                TimeUnit.SECONDS), 1, ThrottleMode.shaping());

        ActorRef actorRef;
        try {
            actorRef = Await.result(actorRefFuture, resolveTime);
        } catch (Exception e) {
            // log exception & tell the sender
            actor.tell("Couldn't stream a book. Timeout occurred.", null);
            // then rethrow it
            throw new ActorInterruptedException(e);
        }

        source.runWith(flow.toMat(Sink.actorRef(actorRef, "completed"), Keep.right()), Server.MATERIALIZER);
    }
}

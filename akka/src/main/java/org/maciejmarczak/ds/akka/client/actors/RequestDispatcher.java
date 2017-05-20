package org.maciejmarczak.ds.akka.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import org.maciejmarczak.ds.akka.model.BookRequest;

import java.util.Arrays;

public class RequestDispatcher extends AbstractActor {

    private final ActorRef responseReceiver
            = context().system().actorOf(Props.create(ResponseReceiver.class), "responseReceiver");
    private final ActorSelection bookManager = context().system().actorSelection(
            "akka.tcp://server@127.0.0.1:3552/user/bookManager");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::dispatchRequest)
                .matchAny(o -> System.out.println("unknown message: " + o))
                .build();
    }

    private void dispatchRequest(String command) {
        String[] cmd = command.trim().split(" ", 2);

        if (!isCmdValid(cmd)) {
            System.out.println("Invalid command: " + command);
            return;
        }

        bookManager.tell(buildRequestFromCmd(cmd), responseReceiver);
    }

    private BookRequest buildRequestFromCmd(String[] cmd) {
        String bookTitle = cmd[1];
        BookRequest.Type type = null;

        switch (cmd[0]) {
            case "o": type = BookRequest.Type.ORDER; break;
            case "s": type = BookRequest.Type.SEARCH; break;
            case "d": type = BookRequest.Type.DOWNLOAD; break;
        }

        return new BookRequest(bookTitle, type);
    }

    private boolean isCmdValid(String[] cmd) {
        boolean hasArgument = cmd.length == 2;
        boolean cmdExists = Arrays.asList("o", "s", "d").contains(cmd[0]);

        return hasArgument && cmdExists;
    }
}

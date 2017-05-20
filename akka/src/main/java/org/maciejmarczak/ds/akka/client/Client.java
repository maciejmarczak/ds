package org.maciejmarczak.ds.akka.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.maciejmarczak.ds.akka.client.actors.ResponseReceiver;
import org.maciejmarczak.ds.akka.model.BookRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private static final ActorSystem SYSTEM;
    private static final ActorRef RESPONSE_RECEIVER;

    static {
        File configFile = new File("conf/client.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system
        SYSTEM = ActorSystem.create("client", config);
        RESPONSE_RECEIVER
                = SYSTEM.actorOf(Props.create(ResponseReceiver.class), "responseReceiver");
    }

    public static void main(String[] args) throws IOException {
        final ActorSelection bookManager = SYSTEM.actorSelection(
                "akka.tcp://server@127.0.0.1:3552/user/bookManager");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if ("q".startsWith(line)) {
                break;
            }
            bookManager.tell(new BookRequest(line, BookRequest.Type.SEARCH), RESPONSE_RECEIVER);
        }

        SYSTEM.terminate();
    }
}

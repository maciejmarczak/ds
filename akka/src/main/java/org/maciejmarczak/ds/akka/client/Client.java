package org.maciejmarczak.ds.akka.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.maciejmarczak.ds.akka.client.actors.RequestDispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private static final ActorSystem SYSTEM;

    static {
        File configFile = new File("conf/client.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system
        SYSTEM = ActorSystem.create("client", config);
    }

    public static void main(String[] args) throws IOException {
        final ActorRef requestDispatcher =
                SYSTEM.actorOf(Props.create(RequestDispatcher.class), "requestDispatcher");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if ("q".startsWith(line)) {
                break;
            }
            requestDispatcher.tell(line, null);
        }

        SYSTEM.terminate();
    }
}

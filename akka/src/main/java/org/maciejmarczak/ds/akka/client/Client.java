package org.maciejmarczak.ds.akka.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

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
        final ActorSelection bookManager = SYSTEM.actorSelection(
                "akka.tcp://server@127.0.0.1:3552/user/bookManager");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if ("q".startsWith(line)) {
                break;
            }
            bookManager.tell(line, null);
        }

        SYSTEM.terminate();
    }
}

package org.maciejmarczak.ds.akka.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.maciejmarczak.ds.akka.server.actors.BookManager;

import java.io.File;

public class Server {

    private static final ActorSystem SYSTEM;
    public static final Materializer MATERIALIZER;

    static {
        File configFile = new File("conf/server.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system
        SYSTEM = ActorSystem.create("server", config);
        MATERIALIZER = ActorMaterializer.create(SYSTEM);
        SYSTEM.actorOf(Props.create(BookManager.class), "bookManager");
    }

    private static void stop() {
        SYSTEM.terminate();
    }

    public static void main(String[] args) {}
}

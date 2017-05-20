package org.maciejmarczak.ds.akka.server.actors;

class BookFinder extends StringReceiver {

    @Override
    void process(String message) {
        System.out.println("find: " + message);
    }
}

package org.maciejmarczak.ds.akka.server.actors;

class OrderMaker extends StringReceiver {

    @Override
    void process(String message) {
        System.out.println("order: " + message);
    }
}

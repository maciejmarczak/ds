package org.maciejmarczak.ds.akka.server.actors;

class BookDownloader extends StringReceiver {

    @Override
    void process(String message) {
        System.out.println("download: " + message);
    }
}

package org.maciejmarczak.ds.rabbitmq;

import java.io.IOException;

interface Actor {
    void publish(String topic, String message) throws IOException;
}

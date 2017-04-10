package org.maciejmarczak.ds.rmq;

import com.rabbitmq.client.*;

import java.io.IOException;

abstract class ChatParticipant implements AutoCloseable {

    final String name;
    Connection connection;
    Channel mainChannel;
    Channel infoChannel;

    ChatParticipant(String name) {
        this.name = name;
    }

    Consumer basicConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("Msg received: " + msg);

                try {
                    Thread.sleep((long)(Math.random() * 10 * 500));
                } catch (InterruptedException ignored) {}
            }
        };
    }

    @Override
    public void close() throws Exception {
        if (infoChannel != null) {
            infoChannel.close();
        }
        if (mainChannel != null) {
            mainChannel.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}

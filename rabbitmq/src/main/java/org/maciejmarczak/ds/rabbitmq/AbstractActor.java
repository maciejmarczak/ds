package org.maciejmarczak.ds.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

import static org.maciejmarczak.ds.rabbitmq.RabbitUtils.EXCHANGE_NAME;

abstract class AbstractActor implements Actor, AutoCloseable {

    final String name;
    Connection connection;
    Channel channel;

    AbstractActor(String name) {
        this.name = name;
    }

    @Override
    public void publish(String topic, String message) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, topic,
                null, message.getBytes());
    }

    final Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
               AMQP.BasicProperties properties, byte[] body) throws IOException {

            String msg = new String(body,"UTF-8");
            System.out.println("Msg received: " + msg);

            try {
                Thread.sleep((long)(Math.random() * 10 * 1000));
            } catch (InterruptedException ignored) {}
            finally {
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        }
    };

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}

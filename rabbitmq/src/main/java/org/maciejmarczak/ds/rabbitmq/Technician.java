package org.maciejmarczak.ds.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Arrays;

import static org.maciejmarczak.ds.rabbitmq.RabbitUtils.EXCHANGE_NAME;

public class Technician extends AbstractActor {

    private static final String QUEUE_NAME = "TECH";
    private final String[] roles;

    private Technician(String name, String[] roles) {
        super(name);
        this.roles = roles;
    }

    private final Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope,
               AMQP.BasicProperties properties, byte[] body) throws IOException {

            String msg = new String(body,"UTF-8");
            System.out.println("Msg received: " + msg);

            String[] cmd = msg.split(":", 2);

            try {
                Thread.sleep((long)(Math.random() * 10 * 1000));
            } catch (InterruptedException ignored) {}
            finally {
                publish("hospital.doctors." + cmd[0], cmd[1]);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        }
    };

    private void start() throws Exception {
        connection = RabbitUtils.getConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for (String role : roles) {
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "hospital.tech." + role);
        }

        channel.basicConsume(QUEUE_NAME, false, consumer);
        channel.basicQos(1);
    }

    public static void main(String[] args) throws Exception {
        new Technician(args[0], Arrays.copyOfRange(args, 1, args.length - 1)).start();
    }
}

package org.maciejmarczak.ds.rmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Arrays;

public class Technician extends ChatParticipant {

    private String[] roles;

    private Technician(String name, String[] roles) {
        super(name);
        this.roles = roles;
    }

    private void start() throws Exception {
        connection = RabbitUtils.getConnection();
        mainChannel = connection.createChannel();
        infoChannel = connection.createChannel();

        mainChannel.exchangeDeclare(RabbitUtils.HOSPITAL_EXCHANGE, "topic");
        infoChannel.exchangeDeclare(RabbitUtils.HOSPITAL_INFO_EXCHANGE, "fanout");

        String queueName = mainChannel.queueDeclare().getQueue();
        infoChannel.queueBind(queueName, RabbitUtils.HOSPITAL_INFO_EXCHANGE, "");
        infoChannel.basicConsume(queueName, false, basicConsumer(infoChannel));

        Consumer techConsumer = techConsumer(mainChannel);
        mainChannel.basicQos(1);

        System.out.println("TECH " + name + " SPEC: " + roles[0] + ", " + roles[1]);
        for (String role: roles) {
            mainChannel.queueDeclare(role, true, false, false, null);
            mainChannel.queueBind(role, RabbitUtils.HOSPITAL_EXCHANGE, "hospital.tech." + role);
            mainChannel.basicConsume(role, false, techConsumer);
        }
    }

    private Consumer techConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("Msg received: " + msg);

                String[] cmd = msg.split(":::");
                String name = cmd[0];
                String content = cmd[1];

                try {
                    Thread.sleep((long)(Math.random() * 10 * 500));
                    System.out.println("Sending response about " + content);
                    channel.basicPublish(RabbitUtils.HOSPITAL_EXCHANGE, "hospital.doctors." + name, null, content.getBytes());
                } catch (InterruptedException ignored) {}
                finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
    }

    public static void main(String[] args) throws Exception {
        new Technician(args[0], Arrays.copyOfRange(args, 1, args.length)).start();
    }

}

package org.maciejmarczak.ds.rabbitmq;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.maciejmarczak.ds.rabbitmq.RabbitUtils.EXCHANGE_NAME;

public class Doctor extends AbstractActor {

    private Doctor(String name) {
        super(name);
    }

    private void start() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        connection = RabbitUtils.getConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "hospital.doctors." + name);
        channel.basicConsume(queueName, false, consumer);

        while (true) {
            System.out.println("Publish > ");
            String line = br.readLine();

            if (line == null || "q".equals(line)) {
                break;
            }

            String[] cmd = line.split(" ", 2);
            publish("hospital.tech." + cmd[0], name + ":" + cmd[1]);
        }
    }

    public static void main(String[] args) throws Exception {
        new Doctor(args[0]).start();
    }
}

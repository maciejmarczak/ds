package org.maciejmarczak.ds.rmq;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Administrator extends ChatParticipant {

    private Administrator(String name) {
        super(name);
    }

    private void start() throws Exception {
        connection = RabbitUtils.getConnection();
        mainChannel = connection.createChannel();
        infoChannel = connection.createChannel();

        mainChannel.exchangeDeclare(RabbitUtils.HOSPITAL_EXCHANGE, "topic");
        infoChannel.exchangeDeclare(RabbitUtils.HOSPITAL_INFO_EXCHANGE, "fanout");

        String queueName = mainChannel.queueDeclare().getQueue();
        mainChannel.queueBind(queueName, RabbitUtils.HOSPITAL_EXCHANGE, "hospital.#");
        mainChannel.basicConsume(queueName, basicConsumer(mainChannel));

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("ADMINISTRATOR " + name);
        while (true) {
            String msg = br.readLine();
            if ("q".equals(msg)) break;

            mainChannel.basicPublish(RabbitUtils.HOSPITAL_INFO_EXCHANGE, "",
                    null, msg.getBytes());
        }
    }

    public static void main(String[] args) throws Exception {
        new Administrator(args[0]).start();
    }

}

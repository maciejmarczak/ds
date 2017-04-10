package org.maciejmarczak.ds.rmq;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Doctor extends ChatParticipant {

    private Doctor(String name) {
        super(name);
    }

    private void start() throws Exception {
        connection = RabbitUtils.getConnection();
        mainChannel = connection.createChannel();
        infoChannel = connection.createChannel();

        mainChannel.exchangeDeclare(RabbitUtils.HOSPITAL_EXCHANGE, "topic");
        infoChannel.exchangeDeclare(RabbitUtils.HOSPITAL_INFO_EXCHANGE, "fanout");

        String queueName = mainChannel.queueDeclare().getQueue();
        mainChannel.queueBind(queueName, RabbitUtils.HOSPITAL_EXCHANGE, "hospital.doctors." + name);
        infoChannel.queueBind(queueName, RabbitUtils.HOSPITAL_INFO_EXCHANGE, "");

        mainChannel.basicConsume(queueName, false, basicConsumer(mainChannel));
        infoChannel.basicConsume(queueName, false, basicConsumer(infoChannel));

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("DOCTOR " + name);
        while (true) {
            String msg = br.readLine();
            if ("q".equals(msg)) break;

            String[] cmd = msg.split("\\s+", 2);
            String finalMsg = name + ":::" + cmd[1];
            mainChannel.basicPublish(RabbitUtils.HOSPITAL_EXCHANGE, "hospital.tech." + cmd[0],
                    null, finalMsg.getBytes());
        }
    }

    public static void main(String[] args) throws Exception {
        new Doctor(args[0]).start();
    }

}

package org.maciejmarczak.ds.rmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

final class RabbitUtils {

    static final String HOSPITAL_EXCHANGE = "HOSPITAL";
    static final String HOSPITAL_INFO_EXCHANGE = "HOSPITAL_INFO";

    private static final ConnectionFactory FACTORY =
            new ConnectionFactory();

    static {
        FACTORY.setHost("localhost");
    }

    static Connection getConnection() throws IOException,
            TimeoutException {
        return FACTORY.newConnection();
    }

}

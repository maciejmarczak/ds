package org.maciejmarczak.ds.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

class RabbitUtils {

    static final String EXCHANGE_NAME = "HOSPITAL";

    private static final ConnectionFactory FACTORY =
            new ConnectionFactory();

    static {
        FACTORY.setHost("localhost");
    }

    static Connection getConnection() throws Exception {
        return FACTORY.newConnection();
    }

}

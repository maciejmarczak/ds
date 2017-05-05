package org.maciejmarczak.ds.rpc.server;

import java.io.IOException;
import java.util.Properties;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        final HospitalServer server = new HospitalServer(getPort());
        server.start();
        server.blockUntilShutdown();
    }

    private static int getPort() {
        int port = 50000;

        Properties properties = new Properties();
        try {
            properties.load(Application.class.getClassLoader()
                    .getResourceAsStream("app.properties"));
            port = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return port;
    }
}

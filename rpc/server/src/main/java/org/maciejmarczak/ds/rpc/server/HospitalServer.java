package org.maciejmarczak.ds.rpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.maciejmarczak.ds.rpc.server.service.LoginService;

import java.io.IOException;

final class HospitalServer {

    private final int port;
    private Server server;

    HospitalServer(int port) {
        this.port = port;
    }

    void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new LoginService())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(HospitalServer.this::stop));
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}

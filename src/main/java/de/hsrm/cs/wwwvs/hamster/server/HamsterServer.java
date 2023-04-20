package de.hsrm.cs.wwwvs.hamster.server;

import io.grpc.Grpc;
import io.grpc.netty.*;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HamsterServer {

    private Server server;

    public void start(String host, int port) throws IOException {
        SocketAddress address = new InetSocketAddress(host, port);
        server = NettyServerBuilder.forAddress(address, InsecureServerCredentials.create())
                .addService(new HamsterService())
                .build()
                .start();

        System.out.println("*** server started");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    HamsterServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.out);
                }
                System.out.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(2, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}

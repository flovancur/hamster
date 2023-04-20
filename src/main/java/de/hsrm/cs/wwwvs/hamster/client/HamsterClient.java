package de.hsrm.cs.wwwvs.hamster.client;

import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.SocketAddress;

public class HamsterClient {

    public HamsterClient(String hostName, int port) {
        var channel = ManagedChannelBuilder.forAddress(hostName, port)
                .proxyDetector(new ProxyDetector() {
                    @Nullable
                    @Override
                    public ProxiedSocketAddress proxyFor(SocketAddress targetServerAddress) throws IOException {
                        return null;
                    }
                })
                .usePlaintext()
                .build();
        // TODO
    }


    public boolean list(String ownerName, String hamsterName) throws StatusRuntimeException {
        // TODO: implement, return false if no hamster found
        return false;
    }

    public void add(String owner, String hamster, short treats) throws StatusRuntimeException {
        // TODO: implement
    }

    public void feed(String owner, String hamster, short treats) throws StatusRuntimeException {
        // TODO: implement
    }

    public void state(String owner, String hamster) throws StatusRuntimeException {
        // TODO: implement
    }

    public void bill(String owner) throws StatusRuntimeException {
        // TODO: implement
    }
}

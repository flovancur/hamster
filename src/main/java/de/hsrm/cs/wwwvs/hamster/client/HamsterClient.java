package de.hsrm.cs.wwwvs.hamster.client;

import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class HamsterClient {

    public HamsterClient(String hostName, int port) {
        var channel = ManagedChannelBuilder.forAddress(hostName, port)
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

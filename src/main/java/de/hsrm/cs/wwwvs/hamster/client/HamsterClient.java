package de.hsrm.cs.wwwvs.hamster.client;

import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.SocketAddress;

public class HamsterClient {

    private final HamsterServiceGrpc.HamsterServiceStub asyncstub;
    private final HamsterServiceGrpc.HamsterServiceBlockingStub blockingStub;
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

        blockingStub = HamsterServiceGrpc.newBlockingStub(channel);
        asyncstub = HamsterServiceGrpc.newStub(channel);
    }


    public boolean list(String ownerName, String hamsterName) throws StatusRuntimeException {
        // TODO: implement, return false if no hamster found
        return false;
    }

    public void add(String owner, String hamster, short treats) throws StatusRuntimeException {
        NewHamsterRequest request = NewHamsterRequest.newBuilder().setOwner(owner).setHamster(hamster).setTreats(treats).build();
        NewHamsterResponse response;
        try{
            response = blockingStub.addHamster(request);
            System.out.println(response.getId());
        }catch(StatusRuntimeException e){
            System.out.println("RPC failed: {0} " + e.getStatus());
        }
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

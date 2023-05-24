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


    public boolean list(String owner, String hamster) throws StatusRuntimeException {
        String new_owner = owner == null ? "": owner;
        String new_hamster = hamster == null ? "": hamster;
        ListHamsterRequest request = ListHamsterRequest.newBuilder().setOwner(new_owner).setHamster(new_hamster).build();
        var response = this.blockingStub.listHamster(request);
        System.out.println("Owner\tName\tPrice\ttreats left");
        try {
            while(response.hasNext()){
                ListHamsterResponse singleResponse = response.next();
                System.out.println(singleResponse.getOwner()+"\t" + singleResponse.getHamster()+"\t" + singleResponse.getCost() +" â‚¬\t" + singleResponse.getTreatsLeft());
            }
        }catch(StatusRuntimeException e){
            System.out.println(e.getStatus().getDescription());
            return false;
        }
        return true;
    }

    public void add(String owner, String hamster, short treats) throws StatusRuntimeException {
        NewHamsterRequest request = NewHamsterRequest.newBuilder().setOwner(owner).setHamster(hamster).setTreats(treats).build();
        NewHamsterResponse response;
        try{
            response = blockingStub.addHamster(request);
            System.out.println(response.getId());
        }catch(StatusRuntimeException e){
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void feed(String owner, String hamster, short treats) throws StatusRuntimeException {
        FeedHamsterRequest request = FeedHamsterRequest.newBuilder().setOwner(owner).setHamster(hamster).setTreats(treats).build();
        FeedHamsterResponse response;
        try{
            response = blockingStub.feedHamster(request);
            System.out.println(response.getTreatsLeft());
        }catch(StatusRuntimeException e){
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void state(String owner, String hamster) throws StatusRuntimeException {
        StateRequest request = StateRequest.newBuilder().setOwner(owner).setHamster(hamster).build();
        StateResponse response;
        try {
            response = this.blockingStub.state(request);
            System.out.println(
                    response.getOwner() +
                            " hamster " +
                            response.getHamster() +
                            " has done " +
                            "has " +
                            response.getTreatsLeft() +
                            " treats left in store " +
                            "price is " +
                            response.getCost() +
                            " "
            );
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }

    };


    public void bill(String owner) throws StatusRuntimeException {
        BillHamsterRequest request = BillHamsterRequest.newBuilder().setOwner(owner).build();
        BillHamsterResponse response;
        try{
            response = blockingStub.billHamster(request);
            System.out.println(response.getPrice());
        } catch(StatusRuntimeException e){
            System.out.println(e.getStatus().getDescription());
        }
    }
}

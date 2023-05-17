package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.*;
import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class HamsterService extends HamsterServiceGrpc.HamsterServiceImplBase {

    private HamsterLib lib = new HamsterLib();

    @Override
    public void addHamster(NewHamsterRequest data, StreamObserver<NewHamsterResponse> responseObserver)    {
        String owner = data.getOwner();
        String hamster = data.getHamster();
        short treats = (short) data.getTreats();

        try{
            int id = lib.new_(owner, hamster, treats);
            NewHamsterResponse response = NewHamsterResponse.newBuilder().setId(id).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (HamsterNameTooLongException | HamsterStorageException | HamsterAlreadyExistsException |
                 HamsterDatabaseCorruptException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void billHamster(BillHamsterRequest data, StreamObserver<BillHamsterResponse> responseObserver){
        String owner = data.getOwner();
        try{
            int price = lib.collect(owner);
            BillHamsterResponse response = BillHamsterResponse.newBuilder().setPrice(price).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (HamsterNameTooLongException | HamsterNotFoundException | HamsterStorageException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void feedHamster(FeedHamsterRequest data, StreamObserver<FeedHamsterResponse> responseObserver){
        String owner = data.getOwner();
        String hamster = data.getHamster();
        short treats = (short) data.getTreats();
        try{
            int id = lib.lookup(owner,hamster);
            int treatsLeft = lib.givetreats(id, treats);
            FeedHamsterResponse response = FeedHamsterResponse.newBuilder().setTreatsLeft(treatsLeft).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (HamsterNameTooLongException | HamsterNotFoundException | HamsterStorageException e) {
            throw new RuntimeException(e.getMessage());
        }

    };


}

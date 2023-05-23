package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.*;
import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

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

        }  catch (HamsterAlreadyExistsException e) {
            Status status = Status.ALREADY_EXISTS.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        } catch (HamsterNameTooLongException e) {
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        } catch (HamsterStorageException | HamsterDatabaseCorruptException e) {
            System.out.println("HamsterStorageException oder HamsterDatabaseCorruptException");
            Status status = Status.UNKNOWN.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }


    };


    @Override
    public void feedHamster(FeedHamsterRequest data, StreamObserver<FeedHamsterResponse> responseObserver){
        String owner = data.getOwner();
        String hamster = data.getHamster();
        short treats = (short) data.getTreats();
        if(hamster.equals("") || owner.equals("")){
            Status status = Status.NOT_FOUND.withDescription(HamsterNotFoundException.DEFAULT_MSG);
            responseObserver.onError(status.asRuntimeException());
        }
        try{
            int id = lib.lookup(owner,hamster);
            int treatsLeft = lib.givetreats(id, treats);
            FeedHamsterResponse response = FeedHamsterResponse.newBuilder().setTreatsLeft(treatsLeft).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (HamsterNotFoundException | HamsterStorageException e) {
            Status status = Status.NOT_FOUND.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        } catch (HamsterNameTooLongException e) {
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
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
        } catch (HamsterNotFoundException | HamsterStorageException e) {
            Status status = Status.NOT_FOUND.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        } catch (HamsterNameTooLongException e) {
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }

    }

    @Override
    public void listHamster(ListHamsterRequest request, StreamObserver<ListHamsterResponse> responseObserver){
        String owner = request.getOwner().equals("") ? null : request.getOwner();
        String hamster = request.getHamster().equals("") ? null : request.getHamster();


        HamsterIterator iterator = lib.iterator();
        ListHamsterResponse response;
        var outOwner = lib.new OutString();
        var outHamster = lib.new OutString();
        var outPrice = lib.new OutShort();
        while (iterator.hasNext()) {
            try {
                int id = lib.directory(iterator, owner, hamster);
                int treats = lib.readentry(id, outOwner,outHamster,outPrice);
                response = ListHamsterResponse.newBuilder()
                        .setOwner(outOwner.getValue())
                        .setHamster(outHamster.getValue())
                        .setCost(outPrice.getValue())
                        .setTreatsLeft(treats)
                        .build();
                responseObserver.onNext(response);
            }catch (HamsterNameTooLongException e) {
                Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                responseObserver.onError(new Throwable(status+"the specified name is too long"));
            }  catch (HamsterNotFoundException e) {
                Status status = Status.NOT_FOUND.withDescription(e.getMessage());
                responseObserver.onError(new Throwable(status+"A hamster or hamster owner could not be found."));
            } catch (HamsterEndOfDirectoryException e) {
                Status status = Status.UNKNOWN.withDescription(e.getMessage());
                responseObserver.onError(new Throwable(status+"EndOfDirectory"));
            }

        }

        responseObserver.onCompleted();
    }

    @Override
    public void state(StateRequest request, StreamObserver<StateResponse> responseObserver) {
        System.out.println("state command received");
        String ownerName = request.getOwner();
        String hamsterName = request.getHamster();

        HamsterIterator iterator = lib.iterator();
        StateResponse response;

        HamsterState stateContainer = new HamsterState();
        try {
            int id = lib.lookup(ownerName, hamsterName);
            int howdoingReturnCode = lib.howsdoing(id, stateContainer);
            response =
                    StateResponse
                            .newBuilder()
                            .setOwner(ownerName)
                            .setHamster(hamsterName)
                            .setTreatsLeft(stateContainer.getTreatsLeft())
                            .setCost(stateContainer.getCost())
                            .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (HamsterException e) {
            responseObserver.onError(Status.ABORTED.withDescription(e.getMessage()).asException());
        }
    }

}

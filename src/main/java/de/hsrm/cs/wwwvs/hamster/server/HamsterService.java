package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.*;
import de.hsrm.cs.wwwvs.hamster.rpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class HamsterService extends HamsterServiceGrpc.HamsterServiceImplBase {

    private HamsterLib lib = new HamsterLib();

}

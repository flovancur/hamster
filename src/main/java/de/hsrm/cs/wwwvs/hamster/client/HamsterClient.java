package de.hsrm.cs.wwwvs.hamster.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

public class HamsterClient {

    public HamsterClient(String hostName, int port) {
        // TODO: implement
    }


    public boolean list(String ownerName, String hamsterName) throws Exception {
        // TODO: print hamster table, return whether list was successful
        return false;
    }

    public void add(String owner, String hamster, short treats) throws Exception {
        // TODO: add hamster, print ID to stdout (or any integer in case it does not matter), only the number
    }

    public void feed(String owner, String hamster, short treats) throws Exception {
        // TODO: feed hamster, print remaining treats to stdout (only the number)
    }

    public void state(String owner, String hamster) throws Exception {
        // TODO: query hamster state, print result to stdout in the format
        // %s's hamster %s has done %d hamster wheel revolutions and has %d treats left in store. Current price is %d €
    }

    public void bill(String owner) throws Exception {
        // TODO: collect hamsters from given owner, print amount to pay to stdout (only the number)
    }
}

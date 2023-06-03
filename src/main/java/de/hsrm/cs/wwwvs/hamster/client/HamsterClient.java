package de.hsrm.cs.wwwvs.hamster.client;

import de.hsrm.cs.wwwvs.hamster.server.HamsterController;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

public class HamsterClient {
    RestTemplate restTemplate;
    String hostName;
    int port;
    public HamsterClient(String hostName, int port) {
        restTemplate = new RestTemplateBuilder().build();
        this.hostName = "http://".concat(hostName.concat(":" + port));
    }


    public boolean list(String ownerName, String hamsterName) throws Exception {
        // TODO: print hamster table, return whether list was successful
        return false;
    }


    public void add(String owner, String hamster, short treats) throws Exception {
        String url = hostName.concat("/hamster");
        AddHamster newHamster = new AddHamster(owner,hamster,treats);
        ResponseEntity<String> response = restTemplate.postForEntity(url, newHamster, String.class);
        System.out.println(response.getBody());
    }
    public record AddHamster(String owner, String hamster,int treats){};

    public void feed(String owner, String hamster, short treats) throws Exception {
        // TODO: feed hamster, print remaining treats to stdout (only the number)
    }

    public void state(String owner, String hamster) throws Exception {
        String url = hostName.concat("/hamster");

        // %s's hamster %s has done %d hamster wheel revolutions and has %d treats left in store. Current price is %d €
    }

    public void bill(String owner) throws Exception {
        // TODO: collect hamsters from given owner, print amount to pay to stdout (only the number)
    }
}

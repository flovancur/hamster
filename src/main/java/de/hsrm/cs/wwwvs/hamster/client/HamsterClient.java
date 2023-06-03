package de.hsrm.cs.wwwvs.hamster.client;

import de.hsrm.cs.wwwvs.hamster.server.HamsterController;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HamsterClient {
    RestTemplate restTemplate;
    String hostName;
    int port;
    public HamsterClient(String hostName, int port) {
        restTemplate = new RestTemplateBuilder().build();
        this.hostName = "http://".concat(hostName.concat(":" + port));
    }


    public boolean list(String ownerName, String hamsterName) throws Exception {
        String url = hostName.concat("/hamster/"+ownerName);
        ResponseEntity<ListHamster[]> response = restTemplate.getForEntity(url, ListHamster[].class);
        System.out.println("Owner\tName\tPrice\ttreats left");
        for(ListHamster entry: response.getBody()){
            System.out.println(entry.owner+"\t"+ entry.hamster+"\t"+entry.price+"\t"+entry.treats);
        }
        return true;
    }
    public record ListHamster(String owner, String hamster, int treats, int price){};


    public void add(String owner, String hamster, short treats) throws Exception {
        String url = hostName.concat("/hamster");
        AddHamster newHamster = new AddHamster(owner,hamster,treats);
        ResponseEntity<String> response = restTemplate.postForEntity(url, newHamster, String.class);
        System.out.println(response.getBody());
    }
    public record AddHamster(String owner, String hamster,int treats){};

    public void feed(String owner, String hamster, short treats) throws Exception {
        // TODO: feed hamster, print remaining treats to stdout (only the number)
        String url = hostName.concat("/hamster/"+owner+"/"+hamster);
        FeedHamster feedHamster = new FeedHamster(treats);
        ResponseEntity<String> response = restTemplate.postForEntity(url, feedHamster, String.class);
        System.out.println(response.getBody());
    }
    public record FeedHamster(int treats){};

    public void state(String owner, String hamster) throws Exception {
        String url = hostName.concat("/hamster/"+owner+"/"+hamster);
        ResponseEntity<StateHamster> response = restTemplate.getForEntity(url,StateHamster.class);
        System.out.println(String.format("%s's hamster %s has done %d hamster wheel revolutions and has %d treats left in store. Current price is %d €",
                response.getBody().owner, response.getBody().hamster, response.getBody().turns, response.getBody().treats, response.getBody().price));
        // %s's hamster %s has done %d hamster wheel revolutions and has %d treats left in store. Current price is %d €
    }
    public record StateHamster(String owner, String hamster, int price, int turns, int treats){};

    public void bill(String owner) throws Exception {
        String url = hostName.concat("/hamster/"+owner);
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.DELETE,null,String.class);
        System.out.println(response.getBody());
        // TODO: collect hamsters from given owner, print amount to pay to stdout (only the number)
    }
}

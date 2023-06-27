package de.hsrm.cs.wwwvs.hamster.server;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HamsterController {



    private static List<Hamster> hamsters = new ArrayList<>();


    @GetMapping("/hamster")
    public List<Hamster> getAllHamsters(@RequestParam(value = "name", required = false) String name) throws Exception {
        return hamsters;
    }

    @PostMapping("/hamster")
    public ResponseEntity<String> addHamster(@RequestBody HamsterAddRequest request, @AuthenticationPrincipal Jwt token) throws Exception {
        if(token != null) {
            System.out.println("Claims: " + token.getClaims());
        }
        var hamster = new Hamster(request.name(),token.getClaims().get("given_name").toString(), request.treats(), 17);
        hamsters.add(hamster);
        return ResponseEntity.created(new URI("http://localhost:4200/hamster")).build();
    }

    @PostMapping("/hamster/{name}")
    public ResponseEntity<TreatsInfo> feed(@PathVariable String name, @RequestBody TreatsInfo treats) throws Exception {
        System.out.println("post mapping /hamster/name triggered");
        for(int i = 0; i < hamsters.size(); i++){
            Hamster hamster = hamsters.get(i);
            if (hamsters.get(i).name.equals(name)) hamsters.set(i, new Hamster(hamster.name(), hamster.owner(), hamster.treatsLeft() - treats.treats, hamster.cost()));
        }
        return ResponseEntity.ok(new TreatsInfo((short)1));
    }

    @DeleteMapping("/hamster")
    public PriceInfo collect() throws Exception {
        return new PriceInfo(42);
    }

    public record Hamster(String name, String owner, int treatsLeft, int cost) {}

    public record HamsterAddRequest(String name, short treats) {}

    public record TreatsInfo(short treats) {}

    public record PriceInfo(int price) {}
}

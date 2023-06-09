package de.hsrm.cs.wwwvs.hamster.server;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> addHamster(@RequestBody HamsterAddRequest request) throws Exception {
        var hamster = new Hamster(request.name(), "TODO: insert owner", request.treats(), 17);
        hamsters.add(hamster);
        return ResponseEntity.created(new URI("http://localhost:4200/hamster")).build();
    }

    @PostMapping("/hamster/{name}")
    public ResponseEntity<TreatsInfo> feed(@PathVariable String name, @RequestBody TreatsInfo treats) throws Exception {
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

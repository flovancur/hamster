package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HamsterController {

    private HamsterLib hamsterLib = new HamsterLib();
    String owner;


    @GetMapping("/hamster")
    public List<Hamster> getAllHamsters(@RequestParam(value = "name", required = false) String name) throws Exception {
        List<Hamster> list = new ArrayList<>();
        name = name.equals("") ? null : name;
        try{
            var outOwner = hamsterLib.new OutString();
            var outHamster = hamsterLib.new OutString();
            var outPrice = hamsterLib.new OutShort();
            HamsterIterator iterator = hamsterLib.iterator();
            while(iterator.hasNext()){
                int id = hamsterLib.directory(iterator,null,name);
                int treats = hamsterLib.readentry(id, outOwner,outHamster,outPrice);
                Hamster entry = new Hamster(outHamster.getValue(),outOwner.getValue(),treats ,outPrice.getValue());
                list.add(entry);
            }
            return list;
        }catch (HamsterEndOfDirectoryException | HamsterNotFoundException ignored) {
            return list;
        } catch (HamsterNameTooLongException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PostMapping("/hamster")
    public ResponseEntity<String> addHamster(@RequestBody HamsterAddRequest request, @AuthenticationPrincipal Jwt token) throws Exception {
        if(token != null) {
            System.out.println("Claims: " + token.getClaims());
            owner=token.getClaims().get("name").toString();
        }
        hamsterLib.new_(owner, request.name, request.treats);
        return ResponseEntity.created(new URI("http://localhost:4200/hamster")).build();
    }

    @PostMapping("/hamster/{name}") //auch name des Owners ermitteln
    public ResponseEntity<TreatsInfo> feed(@PathVariable String name, @RequestBody TreatsInfo treats) throws Exception {
        try {
            int id = hamsterLib.lookup(owner, name);
            int treatsLeft = hamsterLib.givetreats(id, (short)treats.treats());
            return ResponseEntity.ok(new TreatsInfo((short)treatsLeft));
        } catch (HamsterRefusedTreatException e) {
            throw new HamsterException() ;
        }
    }

    @DeleteMapping("/hamster")
    public PriceInfo collect() throws Exception {
        try{
            return new PriceInfo(hamsterLib.collect(owner));
        }catch (HamsterException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleException(ResponseStatusException e) {
        // Return the error message
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    public record Hamster(String name, String owner, int treatsLeft, int cost) {}

    public record HamsterAddRequest(String name, short treats) {}

    public record TreatsInfo(short treats) {}

    public record PriceInfo(int price) {}
}

package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.client.HamsterClient;
import de.hsrm.cs.wwwvs.hamster.lib.*;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

@RestController
public class HamsterController {
    private static Logger logger = LoggerFactory.getLogger(HamsterController.class);
    private HamsterLib hamsterLib = new HamsterLib();
    private static CircuitBreakerRegistry circuitBreakers = CircuitBreakerRegistry
            .custom()
            .addRegistryEventConsumer(new RegistryEventConsumer<CircuitBreaker>() {
                @Override
                public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                    var publisher = entryAddedEvent.getAddedEntry().getEventPublisher();
                    var name = entryAddedEvent.getAddedEntry().getName();

                    publisher.onCallNotPermitted(event -> {
                        logger.warn("Attempted to feed sick hamster {}", name);
                    });
                    publisher.onError(event -> {
                        logger.warn("Hamster {} has refused treats even after retry, we need a veterinarian.", name);
                    });
                    publisher.onStateTransition(event -> {
                        switch (event.getStateTransition().getToState()) {
                            case CLOSED: {
                                logger.warn("Hamster {} has recovered, hurray!", name);
                                break;
                            }
                            case OPEN: {
                                logger.warn("Hamster {} has refused treats even after retry, we need a veterinarian.", name);
                                break;
                            }
                            case HALF_OPEN: {
                                logger.warn("Hamster {} had a rest for a while, maybe we can try another treat", name);
                                break;
                            }
                        }
                    });
                }

                @Override
                public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                }

                @Override
                public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                }
            })
            .build();

    private static RetryRegistry retries = RetryRegistry
            .custom()
            .addRegistryEventConsumer(new RegistryEventConsumer<Retry>() {
                @Override
                public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                    var publisher = entryAddedEvent.getAddedEntry().getEventPublisher();

                    publisher.onRetry(event -> {
                        logger.warn("Hamster {} refused treat, trying again", entryAddedEvent.getAddedEntry().getName());
                    });
                }

                @Override
                public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {
                }

                @Override
                public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {
                }
            })
            .build();

    RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(1)
            .waitDuration(Duration.ofMillis(200))
            .retryExceptions(HamsterException.class)
            .build();

    Retry retryWithConfig = retries.retry("FeedRetry", retryConfig);


    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(3)
                .slidingWindow(3, 1, CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordException(e -> e instanceof HamsterException)
                .build();



    @PostMapping("/hamster")
    public HamsterClient.AddResponse add(@RequestBody HamsterClient.AddHamster hamster){
        try {
            return new HamsterClient.AddResponse(hamsterLib.new_(hamster.owner(), hamster.hamster(), (short) hamster.treats()));
        } catch (HamsterException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // CircuitBreakerFactory

    @PostMapping("/hamster/{owner}/{hamster}")
    public HamsterClient.FeedResponse feed(@PathVariable String owner, @PathVariable String hamster,@RequestBody HamsterClient.FeedHamster treats) throws HamsterException{
        Retry retryWithConfig = retries.retry(String.format("%s from %s", hamster, owner), retryConfig);
        CircuitBreaker circuitBreakerWithConfig = circuitBreakers.circuitBreaker(String.format("%s from %s", hamster, owner), circuitBreakerConfig);

        Callable<HamsterClient.FeedResponse> myCallable = () -> {
            try {
                int id = hamsterLib.lookup(owner, hamster);
                int treatsLeft = hamsterLib.givetreats(id, (short)treats.treats());
                return new HamsterClient.FeedResponse(treatsLeft);
            } catch (HamsterRefusedTreatException e) {
                throw new HamsterException() ;
            }
        };

        Callable<HamsterClient.FeedResponse> decoratedCallable = Decorators.ofCallable(myCallable)
                .withCircuitBreaker(circuitBreakerWithConfig)
                .withRetry(retryWithConfig)
                .decorate();

        try {
            return decoratedCallable.call();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    @GetMapping("/hamster/{owner}/{hamster}")
    public HamsterClient.StateHamster state(@PathVariable String owner, @PathVariable String hamster){
        try{
            int id = hamsterLib.lookup(owner, hamster);
            HamsterState hamsterState = new HamsterState();
            int success = hamsterLib.howsdoing(id, hamsterState);
            return new HamsterClient.StateHamster(owner, hamster, hamsterState.getCost(), hamsterState.getRounds(),hamsterState.getTreatsLeft());

        }catch (HamsterException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/hamster/{owner}")
    public HamsterClient.BillResponse bill(@PathVariable String owner){
        try{
            return new HamsterClient.BillResponse(hamsterLib.collect(owner));
        }catch (HamsterException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/hamster/{owner}")
    public List list(@PathVariable String owner, @RequestParam(value = "name", required = false, defaultValue = "null") String hamster){
        List<HamsterClient.ListHamster> response = new ArrayList<>();
        owner = owner.equals("null") ? null : owner;
        hamster = hamster.equals("null") ? null : hamster;
        try{
            var outOwner = hamsterLib.new OutString();
            var outHamster = hamsterLib.new OutString();
            var outPrice = hamsterLib.new OutShort();
            HamsterIterator iterator = hamsterLib.iterator();
            while(iterator.hasNext()){
                int id = hamsterLib.directory(iterator,owner,hamster);
                int treats = hamsterLib.readentry(id, outOwner,outHamster,outPrice);
                HamsterClient.ListHamster entry = new HamsterClient.ListHamster(outOwner.getValue(),outHamster.getValue(),treats ,outPrice.getValue());
                response.add(entry);
            }
            return response;
        }catch (HamsterEndOfDirectoryException ignored) {
            return response;
        } catch (HamsterNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No hamsters matching criteria found");
        } catch (HamsterNameTooLongException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/hamster")
    public List list2(@RequestParam(value = "name", required = false, defaultValue = "null") String hamster){
        List<HamsterClient.ListHamster> response = new ArrayList<>();
        hamster = hamster.equals("null") ? null : hamster;
        try{
            var outOwner = hamsterLib.new OutString();
            var outHamster = hamsterLib.new OutString();
            var outPrice = hamsterLib.new OutShort();
            HamsterIterator iterator = hamsterLib.iterator();
            while(iterator.hasNext()){
                int id = hamsterLib.directory(iterator,null,hamster);
                int treats = hamsterLib.readentry(id, outOwner,outHamster,outPrice);
                HamsterClient.ListHamster entry = new HamsterClient.ListHamster(outOwner.getValue(),outHamster.getValue(),treats ,outPrice.getValue());
                response.add(entry);
            }
            return response;
        }catch (HamsterEndOfDirectoryException ignored) {
            return response;
        } catch (HamsterNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No hamsters matching criteria found");
        } catch (HamsterNameTooLongException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleException(ResponseStatusException e) {
        // Return the error message
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}



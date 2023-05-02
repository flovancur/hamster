package de.hsrm.cs.wwwvs.hamster.server;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@RestController
public class HamsterController {

    private static Logger logger = LoggerFactory.getLogger(HamsterController.class);

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

    // TODO: Add endpoints
}

package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithVirtualTime(){

        //will use your build time
        //test will work very fast
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
}

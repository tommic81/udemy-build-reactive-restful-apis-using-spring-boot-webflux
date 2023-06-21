package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log(); // starts from 0 -->  ....N

        infiniteFlux.subscribe((element) -> System.out.println("Value is : " + element));

        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3) //takes 3 elements
                .log(); // starts from 0 -->  ....N

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l ->  Integer.valueOf(l.intValue()))
                .take(3) //takes 3 elements
                .log(); // starts from 0 -->  ....N

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
    @Test
    public void infiniteSequenceMap_withDelay() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l ->  Integer.valueOf(l.intValue()))
                .take(3) //takes 3 elements
                .log(); // starts from 0 -->  ....N

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

}

package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> {
                    System.out.println("Exception is : " + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default", "default1")
                .verifyComplete();
                //.expectError(RuntimeException.class)
                //.verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
        //.expectError(RuntimeException.class)
        //.verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
        //.expectError(RuntimeException.class)
        //.verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
        //.expectError(RuntimeException.class)
        //.verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5))); //waits 5s before retry

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
        //.expectError(RuntimeException.class)
        //.verify();
    }
}

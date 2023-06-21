package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFLux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFLux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext( 1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFLux = Flux.range(1, 10).log();

        finiteFLux.subscribe((element) -> System.out.println("Element is : " + element),
                (e) -> System.err.println("Exception is : " + e),
                () -> System.out.println("Done"),
                (subscription -> subscription.request(2))); //sent me 2 elements
    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> finiteFLux = Flux.range(1, 10).log();

        finiteFLux.subscribe((element) -> System.out.println("Element is : " + element),
                (e) -> System.err.println("Exception is : " + e),
                () -> System.out.println("Done"),
                (subscription -> subscription.cancel()));
    }

    @Test
    public void customized_backPressure() {
        Flux<Integer> finiteFLux = Flux.range(1, 10).log();

        finiteFLux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is : " + value);
                if(value == 4){
                    cancel();
                }
            }
        });
    }
}

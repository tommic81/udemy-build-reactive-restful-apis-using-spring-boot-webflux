package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {
    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s-> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s-> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s-> s.length())
                .repeat(1) //repeating  the same flux
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5,4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length()>4)
                .map(s-> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log();

        StepVerifier.create(stringFlux)

                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
    @Test
    public void transformUsingFlatMap_usingparallel() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
                .window(2) //(A,B), (C, D)
                .flatMap((s) -> s.map(this::convertToList).subscribeOn(parallel())) //parallel execution
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    public void transformUsingFlatMap_usingparallel_maintain_order() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B", "C", "D", "E", "F"))
                .window(2) //(A,B), (C, D)
                //keep order
                .flatMapSequential((s) -> s.map(this::convertToList).subscribeOn(parallel())) //parallel execution
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
}

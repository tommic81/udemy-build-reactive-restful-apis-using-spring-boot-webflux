package com.learnreactivespring.fluxandmonoplayground;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
    @Test
    public void fluxTest() {

        Flux<String> stringFlux = Flux.just("String", "String Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))//attaching error to Flux
                .concatWith(Flux.just("After Error")) // will not be executed after error
                .log(); //logging all events

        stringFlux.subscribe(System.out::println, //onNext
                (e) -> System.err.println(e), // onError
                () -> System.out.println("Completed")); // onComplete

    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("String", "String Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        //assertions
        StepVerifier.create(stringFlux)
                .expectNext("String")
                .expectNext("String Boot")
                .expectNext("Reactive Spring")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();//starting verification flow
        // .verifyComplete(); //starting verification flow and validates complete

    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("String", "String Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        //assertions
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                .verify();//starting verification flow
    }

    @Test
    public void fluxTestElements_WithError1() {
        Flux<String> stringFlux = Flux.just("String", "String Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        //assertions
        StepVerifier.create(stringFlux)
                .expectNext("String", "String Boot", "Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                .verify();//starting verification flow
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring").log();

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();

    }

    @Test
    public void monoTest_Error() {

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();

    }
}

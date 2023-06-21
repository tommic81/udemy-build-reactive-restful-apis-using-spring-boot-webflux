package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles("test")
class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(ItemCapped.class)
                .then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped()))
                .block();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item " + 1, 100.00 + i));
        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(itemCapped -> log.info("Inserted item is " + itemCapped))
        .blockLast();
    }

    @Test
    public void testStreamAllItems(){
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

    /*    StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();*/

    }
}
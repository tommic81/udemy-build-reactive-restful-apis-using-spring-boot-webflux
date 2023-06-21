package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99));

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is :" + item);
                })
                .blockLast();//waiting for all inserts
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByID() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription :"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> saveItem = itemReactiveRepository.save(item);

        StepVerifier.create(saveItem.log("saveItem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 520.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV").log("findItemByDescription :")
                .map(item -> {
                    item.setPrice(newPrice); //setting the new price
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item)); //saving the item with the new price

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.00)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new item list : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItemB() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap((item) -> itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new item list : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}

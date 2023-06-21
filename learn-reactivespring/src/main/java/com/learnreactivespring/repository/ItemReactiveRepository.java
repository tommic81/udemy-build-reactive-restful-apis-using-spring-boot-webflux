package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
    Mono<Item> findByDescription(String description);
}

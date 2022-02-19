package com.github.iamhi.hizone.authentication.data;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {

    Mono<UserEntity> findByUsername(String username);
}

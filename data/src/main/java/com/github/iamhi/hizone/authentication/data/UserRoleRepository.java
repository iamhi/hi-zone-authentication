package com.github.iamhi.hizone.authentication.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRoleEntity, Integer> {

    Flux<UserRoleEntity> findByUsername(String username);

    Mono<UserRoleEntity> findByUuid(String uuid);
}

package com.github.iamhi.hizone.authentication.data;

import reactor.core.publisher.Mono;

public interface TokenRepository {

    Mono<String> createToken(String key, long expiration);

    Mono<String> deleteToken(String key);

    Mono<String> hasToken(String key);
}

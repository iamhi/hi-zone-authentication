package com.github.iamhi.hizone.authentication.data;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public record RedisTokenRepository(
    RedisRepository redisRepository
) implements TokenRepository {

    @Override
    public Mono<String> createToken(String token, long expiration) {
        return redisRepository.getReactiveConnection().setex(token, expiration, "true").thenReturn(token);
    }

    @Override
    public Mono<String> deleteToken(String key) {
        return redisRepository.getReactiveConnection().getdel(key);
    }

    @Override
    public Mono<String> hasToken(String token) {
        return redisRepository.getReactiveConnection().get(token);
    }
}

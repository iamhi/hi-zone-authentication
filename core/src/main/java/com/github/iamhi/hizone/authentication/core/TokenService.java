package com.github.iamhi.hizone.authentication.core;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TokenService {

    long REFRESH_TOKEN_EXPIRATION = (long) 1000 * 60 * 60 * 24 * 3;

    long ACCESS_TOKEN_EXPIRATION = (long) 1000 * 60 * 30;

    Mono<String> createToken(Map<String, Object> payload, long lifeTime);

    Mono<Claims> decodeToken(String token);

    void invalidateToken(String token);
}

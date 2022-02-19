package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public record TokenServiceImpl(
    TokenConfig tokenConfig
) implements TokenService {

    @Override
    public Mono<String> createToken(Map<String, Object> payload, long lifeTime) {
        // add to db
        return Mono.just(encodeRefreshToken(payload, lifeTime));
    }

    @Override
    public Mono<Claims> decodeToken(String token) {
        return Mono.just(Jwts.parser()
            .setSigningKey(tokenConfig.getTokenSecret())
            .parseClaimsJws(token).getBody());
    }

    @Override
    public void invalidateToken(String token) {
        // remove from redis
        // remove from mongodb
        // return OK
        // return Mono.just("ok");

        System.out.println("Invalidating token: " + token);
    }

    private String encodeRefreshToken(Map<String, Object> payload, long lifeTime) {
        return Jwts.builder()
            .setClaims(new HashMap<>(payload))
            .setSubject(payload.get("uuid").toString())
            .setExpiration(new Date(Instant.now().toEpochMilli() + lifeTime))
            .signWith(SignatureAlgorithm.HS256, tokenConfig.getTokenSecret())
            .compact();
    }
}

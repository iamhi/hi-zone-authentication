package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.TokenConfig;
import com.github.iamhi.hizone.authentication.data.TokenRepository;
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
    TokenConfig tokenConfig,
    TokenRepository tokenRepository
) implements TokenService {

    @Override
    public Mono<String> createToken(Map<String, Object> payload, long lifeTime) {
        return Mono.just(encodeRefreshToken(payload, lifeTime))
            .flatMap(token -> tokenRepository.createToken(token, lifeTime));
    }

    @Override
    public Mono<Claims> decodeToken(String token) {
        return validateToken(token).map(validatedToken -> Jwts.parser()
            .setSigningKey(tokenConfig.getTokenSecret())
            .parseClaimsJws(validatedToken).getBody());
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.deleteToken(token).block();

        System.out.println("Invalidating token: " + token);
    }

    @Override
    public Mono<String> validateToken(String token) {
        return tokenRepository.hasToken(token).flatMap(hasToken ->
            Boolean.TRUE.equals(Boolean.parseBoolean(hasToken)) ?
                Mono.just(token) : Mono.error(new RuntimeException()));
    }

    @Override
    public Mono<String> extendToken(String token, long lifeTime) {
        return tokenRepository.createToken(token, lifeTime);
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

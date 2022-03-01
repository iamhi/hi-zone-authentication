package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TokenService {

    long REFRESH_TOKEN_EXPIRATION = (long) 1000 * 60 * 60 * 24 * 3;

    long ACCESS_TOKEN_EXPIRATION = (long) 1000 * 60 * 30;

    String USER_UUID = "uuid";

    String USER_USERNAME = "username";

    String USER_ROLES = "roles";

    Mono<String> createToken(Map<String, Object> payload, long lifeTime);

    Mono<Claims> decodeToken(String token);

    void invalidateToken(String token);

    Mono<String> validateToken(String token);

    Mono<String> extendToken(String token, long lifeTime);

    Map<String, Object> getAccessTokenPayload(UserDTO userDTO);

    Map<String, Object> getRefreshTokenPayload(UserDTO userDTO);
}

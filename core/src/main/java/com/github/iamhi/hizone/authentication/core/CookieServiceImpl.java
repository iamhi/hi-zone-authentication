package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.CookieConfig;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.github.iamhi.hizone.authentication.core.TokenService.ACCESS_TOKEN_EXPIRATION;
import static com.github.iamhi.hizone.authentication.core.TokenService.REFRESH_TOKEN_EXPIRATION;

@Service
public record CookieServiceImpl(
    UserService userService,
    TokenService tokenService,
    CookieConfig cookieConfig
) implements CookieService {

    private static final String USER_UUID = "uuid";
    private static final String USER_USERNAME = "username";
    private static final String USER_ROLES = "roles";

    @Override
    public Mono<ResponseCookie> createAccessCookie(UserDTO userDTO) {
        return tokenService
            .createToken(getAccessTokenPayload(userDTO), ACCESS_TOKEN_EXPIRATION)
            .map(this::createResponseAccessCookie);
    }

    @Override
    public Mono<ResponseCookie> createAccessCookie(String refreshToken) {
        return tokenService.decodeToken(refreshToken)
            .map(this::getUserUuidFromClaims)
            .flatMap(userService::findUser)
            .flatMap(this::createAccessCookie);
    }

    @Override
    public Mono<ResponseCookie> createRefreshCookie(UserDTO userDTO) {
        return tokenService
            .createToken(getRefreshTokenPayload(userDTO), REFRESH_TOKEN_EXPIRATION)
            .map(this::createResponseRefreshCookie);
    }

    @Override
    public Mono<ResponseCookie> createRefreshCookie(String refreshToken) {
        return tokenService.decodeToken(refreshToken)
            .map(this::getUserUuidFromClaims)
            .flatMap(userService::findUser)
            .flatMap(this::createRefreshCookie);
    }

    @Override
    public Mono<UserDTO> getUserFromAccessToken(MultiValueMap<String, HttpCookie> cookies) {
        return Mono.justOrEmpty(cookies.getFirst(ACCESS_TOKEN_COOKIE_NAME))
            .map(HttpCookie::getValue).flatMap(tokenService()::decodeToken)
            .map(this::getUserUuidFromClaims)
            .flatMap(userService::findUser);
    }

    @Override
    public ResponseCookie createExpiredRefreshCookie() {
        return invalidateCookie(REFRESH_TOKEN_COOKIE_NAME, cookieConfig.getRefreshTokenPath());
    }

    @Override
    public ResponseCookie createExpiredAccessCookie() {
        return invalidateCookie(ACCESS_TOKEN_COOKIE_NAME, cookieConfig.getAccessTokenPath());
    }

    private String getUserUuidFromClaims(Claims claims) {
        return claims.get(USER_UUID, String.class);
    }

    private Map<String, Object> getAccessTokenPayload(UserDTO userDTO) {
        return Map.of(
            USER_UUID, userDTO.uuid(),
            USER_USERNAME, userDTO.username(),
            USER_ROLES, userDTO.roles()
        );
    }

    private Map<String, Object> getRefreshTokenPayload(UserDTO userDTO) {
        return Map.of(
            USER_UUID, userDTO.uuid()
        );
    }

    private ResponseCookie createResponseRefreshCookie(String token) {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, token, cookieConfig.getRefreshTokenPath(), TokenService.REFRESH_TOKEN_EXPIRATION / 1000);
    }

    private ResponseCookie createResponseAccessCookie(String token) {
        return createCookie(ACCESS_TOKEN_COOKIE_NAME, token, cookieConfig.getAccessTokenPath(), ACCESS_TOKEN_EXPIRATION / 1000);
    }

    private ResponseCookie invalidateCookie(String name, String path) {
        return createCookie(name, "", path, 0);
    }

    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
//            .sameSite(Cookie.SameSite.STRICT.attributeValue())
//            .httpOnly(true)
            .path(path)
            .maxAge(maxAge)
            .build();
    }
}
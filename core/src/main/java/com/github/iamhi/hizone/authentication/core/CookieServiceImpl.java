package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.CookieConfig;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import static com.github.iamhi.hizone.authentication.core.TokenService.ACCESS_TOKEN_EXPIRATION;
import static com.github.iamhi.hizone.authentication.core.TokenService.REFRESH_TOKEN_EXPIRATION;

@Service
record CookieServiceImpl(
    UserService userService,
    TokenService tokenService,
    CookieConfig cookieConfig
) implements CookieService {

    @Override
    public Mono<ResponseCookie> createAccessCookie(UserDTO userDTO) {
        return tokenService
            .createToken(tokenService.getAccessTokenPayload(userDTO), ACCESS_TOKEN_EXPIRATION)
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
            .createToken(tokenService.getRefreshTokenPayload(userDTO), REFRESH_TOKEN_EXPIRATION)
            .map(this::createResponseRefreshCookie);
    }

    @Override
    public Mono<UserDTO> getUserFromAccessToken(MultiValueMap<String, HttpCookie> cookies) {
        return Mono.justOrEmpty(cookies.getFirst(ACCESS_TOKEN_COOKIE_NAME))
            .map(HttpCookie::getValue).flatMap(tokenService()::decodeToken)
            .map(this::getUserUuidFromClaims)
            .flatMap(userService::findUser);
    }

    @Override
    public Mono<String> getRefreshToken(MultiValueMap<String, HttpCookie> cookies) {
        return Mono.justOrEmpty(cookies.getFirst(REFRESH_TOKEN_COOKIE_NAME)).map(HttpCookie::getValue);
    }

    @Override
    public Mono<ResponseCookie> createExpiredRefreshCookie() {
        return Mono.just(invalidateCookie(REFRESH_TOKEN_COOKIE_NAME, cookieConfig.getRefreshTokenPath()));
    }

    @Override
    public Mono<ResponseCookie> createExpiredAccessCookie() {
        return Mono.just(invalidateCookie(ACCESS_TOKEN_COOKIE_NAME, cookieConfig.getAccessTokenPath()));
    }

    private String getUserUuidFromClaims(Claims claims) {
        return claims.get(TokenService.USER_UUID, String.class);
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
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
            .path(path)
            .maxAge(maxAge);

        if (cookieConfig.isUseHttpSecure()) {
            builder.secure(true);
            builder.httpOnly(true);
        }

        return builder.build();
    }
}

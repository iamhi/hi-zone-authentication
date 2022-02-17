package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public interface CookieService {

    String ACCESS_TOKEN_COOKIE_NAME = "messaging-club-access-token";

    String REFRESH_TOKEN_COOKIE_NAME = "messaging-club-refresh-token";

    Mono<ResponseCookie> createAccessCookie(UserDTO userDTO);

    Mono<ResponseCookie> createAccessCookie(String refreshToken);

    Mono<ResponseCookie> createRefreshCookie(UserDTO userDTO);

    Mono<ResponseCookie> createRefreshCookie(String refreshToken);

    Mono<UserDTO> getUserFromAccessToken(MultiValueMap<String, HttpCookie> cookies);

    ResponseCookie createExpiredRefreshCookie();

    ResponseCookie createExpiredAccessCookie();
}
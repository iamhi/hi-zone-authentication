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

    Mono<UserDTO> getUserFromAccessToken(MultiValueMap<String, HttpCookie> cookies);

    Mono<String> getRefreshToken(MultiValueMap<String, HttpCookie> cookies);

    Mono<ResponseCookie> createExpiredRefreshCookie();

    Mono<ResponseCookie> createExpiredAccessCookie();
}

package com.github.iamhi.hizone.authentication.gateway.shared;

import com.github.iamhi.hizone.authentication.core.CookieService;
import com.github.iamhi.hizone.authentication.core.TokenService;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
record SharedCookieHelperImpl(
    TokenService tokenService,
    CookieService cookieService
) implements SharedCookieHelper {

    @Override
    public Mono<ServerResponse.BodyBuilder> addNewCookies(ServerResponse.BodyBuilder responseBuilder, UserDTO userDTO) {
        return Mono.zip(
            cookieService.createAccessCookie(userDTO),
            cookieService.createRefreshCookie(userDTO)
        ).map(cookiesTuple -> responseBuilder.cookie(cookiesTuple.getT1()).cookie(cookiesTuple.getT2()));
    }

    @Override
    public Mono<ServerResponse.BodyBuilder> addCookieFromRefresh(ServerResponse.BodyBuilder responseBuilder, String refreshToken) {
        return cookieService.createRefreshCookie(refreshToken).map(responseBuilder::cookie);
    }

    @Override
    public Mono<ServerResponse.BodyBuilder> invalidateCookies(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest) {
        invalidateTokens(serverRequest.cookies());

        return Mono.zip(
                cookieService.createExpiredAccessCookie(),
                cookieService.createExpiredRefreshCookie())
            .map(cookieTuple -> responseBuilder
                .cookie(cookieTuple.getT1())
                .cookie(cookieTuple.getT2()));
    }

    public void invalidateTokens(MultiValueMap<String, HttpCookie> cookies) {
        cookies.values().stream().flatMap(List::stream)
            .map(HttpCookie::getValue).forEach(tokenService::invalidateToken);
    }
}

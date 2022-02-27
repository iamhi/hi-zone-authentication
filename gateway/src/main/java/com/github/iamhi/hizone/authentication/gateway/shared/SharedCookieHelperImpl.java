package com.github.iamhi.hizone.authentication.gateway.shared;

import com.github.iamhi.hizone.authentication.config.TokenConfig;
import com.github.iamhi.hizone.authentication.core.CookieService;
import com.github.iamhi.hizone.authentication.core.TokenService;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import com.github.iamhi.hizone.authentication.core.models.UserRoleEnum;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
record SharedCookieHelperImpl(
    TokenService tokenService,
    CookieService cookieService,
    TokenConfig tokenConfig
) implements SharedCookieHelper {

    @Override
    public Mono<ServerResponse.BodyBuilder> addNewCookies(ServerResponse.BodyBuilder responseBuilder, UserDTO userDTO) {
        return Mono.zip(
            cookieService.createAccessCookie(userDTO),
            cookieService.createRefreshCookie(userDTO)
        ).map(cookiesTuple -> responseBuilder.cookie(cookiesTuple.getT1()).cookie(cookiesTuple.getT2()));
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

    @Override
    public Mono<ServerResponse.BodyBuilder> refreshAccessToken(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest) {
        return cookieService()
            .getRefreshToken(serverRequest.cookies())
            .flatMap(tokenService::validateToken)
            .flatMap(refreshToken -> tokenService.extendToken(refreshToken, tokenConfig.getRefreshTokenLife()))
            .flatMap(cookieService::createAccessCookie).map(responseBuilder::cookie);
    }

    @Override
    public Mono<Boolean> isRole(ServerRequest serverRequest, String role) {
        return cookieService.getUserFromAccessToken(serverRequest.cookies())
            .flatMap(userDTO -> (userDTO.roles().contains(UserRoleEnum.valueOf(role)) ?
                Mono.just(true) : Mono.error(new RuntimeException())));
    }

    public void invalidateTokens(MultiValueMap<String, HttpCookie> cookies) {
        cookies.values().stream().flatMap(List::stream)
            .map(HttpCookie::getValue).forEach(tokenService::invalidateToken);
    }
}

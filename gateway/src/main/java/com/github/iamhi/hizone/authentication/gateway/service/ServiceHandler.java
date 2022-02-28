package com.github.iamhi.hizone.authentication.gateway.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public record ServiceHandler() {

    Mono<ServerResponse> login(ServerRequest serverRequest) {
        return null;
    }

    Mono<ServerResponse> refreshToken(ServerRequest serverRequest) {
        return null;
    }

    Mono<ServerResponse> getUserInfo(ServerRequest serverRequest) {
        return null;
    }
}

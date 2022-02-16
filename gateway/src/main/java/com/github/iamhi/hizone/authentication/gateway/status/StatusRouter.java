package com.github.iamhi.hizone.authentication.gateway.status;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class StatusRouter {

    private static final String ROUTER_PREFIX = "/status";

    @Bean
    public RouterFunction<ServerResponse> composePingRoute() {
        return route(GET(ROUTER_PREFIX + "/ping"), serverRequest ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Map.of("ping", "Pong from authentication")), Map.class));
    }
}

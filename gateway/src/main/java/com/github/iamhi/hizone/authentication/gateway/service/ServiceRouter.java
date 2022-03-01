package com.github.iamhi.hizone.authentication.gateway.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ServiceRouter {

    private static final String ROUTER_PREFIX = "/service";
    private static final String SERVICE_LOGIN_ROUTE = ROUTER_PREFIX + "/login";
    private static final String SERVICE_USER_INFO = ROUTER_PREFIX + "/userinfo";

    @Bean
    public RouterFunction<ServerResponse> serviceRouterCompose(ServiceHandler serviceHandler) {
        return route(POST(SERVICE_LOGIN_ROUTE).and(accept(MediaType.APPLICATION_JSON)), serviceHandler::login).and(route(POST(SERVICE_USER_INFO).and(accept(MediaType.APPLICATION_JSON)), serviceHandler::getUserInfo));
    }
}

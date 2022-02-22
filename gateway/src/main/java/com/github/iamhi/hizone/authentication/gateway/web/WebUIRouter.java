package com.github.iamhi.hizone.authentication.gateway.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class WebUIRouter {

    private static final String ROUTER_PREFIX = "/ui";

    @Bean
    public RouterFunction<ServerResponse> composeWebUIRoute() {
        return route(GET(ROUTER_PREFIX + "/login"), serverRequest ->
            ServerResponse.ok().render("LoginFormView"));
    }
}

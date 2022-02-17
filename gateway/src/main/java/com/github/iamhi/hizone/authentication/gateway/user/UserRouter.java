package com.github.iamhi.hizone.authentication.gateway.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {
    private static final String ROUTER_PREFIX = "/user";
    private static final String CREATE_ROUTE = ROUTER_PREFIX + "/create";
    private static final String LOGIN_ROUTE = ROUTER_PREFIX + "/";
    private static final String ME_ROUTE = ROUTER_PREFIX + "/me";
    private static final String ADD_ADMIN_ROUTE = ROUTER_PREFIX + "/makemeadmin";

    @Bean
    public RouterFunction<ServerResponse> userRouterCompose(UserHandler userHandler) {
        return route(POST(CREATE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), userHandler::create)
            .and(route(POST(LOGIN_ROUTE).and(accept(MediaType.APPLICATION_JSON)), userHandler::login))
            .and(route(GET(ME_ROUTE), userHandler::me))
            .and(route(GET(ADD_ADMIN_ROUTE), userHandler::addAdminRole));
    }
}
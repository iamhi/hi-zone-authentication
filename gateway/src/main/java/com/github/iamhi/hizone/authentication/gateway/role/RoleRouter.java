package com.github.iamhi.hizone.authentication.gateway.role;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoleRouter {

    private static final String ROUTER_PREFIX = "/role";
    private static final String ADD_ROLE_ROUTE = ROUTER_PREFIX + "/addrole";

    @Bean
    public RouterFunction<ServerResponse> roleRouterCompose(RoleHandler roleHandler) {
        return route(POST(ADD_ROLE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), roleHandler::addRole);
    }
}

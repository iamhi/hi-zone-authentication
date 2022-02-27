package com.github.iamhi.hizone.authentication.gateway.shared;

import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface SharedCookieHelper {

    Mono<ServerResponse.BodyBuilder> addNewCookies(ServerResponse.BodyBuilder responseBuilder, UserDTO userDTO);

    Mono<ServerResponse.BodyBuilder> invalidateCookies(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest);

    Mono<ServerResponse.BodyBuilder> refreshAccessToken(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest);

    Mono<Boolean> isRole(ServerRequest serverRequest, String role);
}

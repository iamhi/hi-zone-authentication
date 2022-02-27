package com.github.iamhi.hizone.authentication.gateway.role;

import com.github.iamhi.hizone.authentication.api.requests.AddRoleRequest;
import com.github.iamhi.hizone.authentication.core.CookieService;
import com.github.iamhi.hizone.authentication.core.UserService;
import com.github.iamhi.hizone.authentication.core.models.UserRoleEnum;
import com.github.iamhi.hizone.authentication.gateway.shared.SharedCookieHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public record RoleHandler(
    SharedCookieHelper sharedCookieHelper,
    CookieService cookieService,
    UserService userService
) {
    Mono<ServerResponse> addRole(ServerRequest serverRequest) {
        return sharedCookieHelper.isRole(serverRequest, UserRoleEnum.ADMIN.name())
            .flatMap(isRole -> serverRequest.bodyToMono(AddRoleRequest.class))
            .flatMap(addRoleRequest -> userService.addRole(addRoleRequest.username(), addRoleRequest.role()))
            .flatMap(roleAdded -> ServerResponse.ok().build())
            .onErrorResume(err -> ServerResponse.badRequest().bodyValue(err));
    }
}

package com.github.iamhi.hizone.authentication.gateway.user;

import com.github.iamhi.hizone.authentication.api.requests.CreateUserRequest;
import com.github.iamhi.hizone.authentication.api.requests.LoginUserRequest;
import com.github.iamhi.hizone.authentication.api.responses.CreateUserDeniedResponse;
import com.github.iamhi.hizone.authentication.api.responses.LoginDeniedResponse;
import com.github.iamhi.hizone.authentication.api.responses.UserResponse;
import com.github.iamhi.hizone.authentication.core.UserService;
import com.github.iamhi.hizone.authentication.core.exceptions.UserNotFoundThrowable;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public record UserHandler(
    UserService userService
) {
    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRequest.class)
            .flatMap(createUserRequest ->
                userService.createUser(
                    createUserRequest.username(),
                    createUserRequest.password(),
                    createUserRequest.email()
                ))
            .flatMap(this::loginSuccessful)
            .onErrorResume(this::loginDenied);
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginUserRequest.class).flatMap(
                loginRequest -> userService.userLogin(loginRequest.username(), loginRequest.password()))
            .flatMap(this::loginSuccessful)
            .onErrorResume(this::loginDenied);
    }

    public Mono<ServerResponse> me(ServerRequest serverRequest) {
        // This needs to implemented after tokens
        return ServerResponse.ok().bodyValue("OK");
    }

    public Mono<ServerResponse> addAdminRole(ServerRequest serverRequest) {
        // This needs to implemented after tokens
        return ServerResponse.ok().bodyValue("OK");
    }

    private Mono<ServerResponse> loginSuccessful(UserDTO userDTO) {
        return ServerResponse.ok().bodyValue(new UserResponse(
            userDTO.uuid(),
            userDTO.username(),
            userDTO.email()
        ));
    }

    private Mono<ServerResponse> loginDenied(Throwable throwable) {
        if (throwable.getClass().equals(UserNotFoundThrowable.class)) {
            return invalidateTokens(ServerResponse.badRequest())
                .contentType(MediaType.APPLICATION_JSON).bodyValue(new LoginDeniedResponse());
        }

        if (throwable.getClass().equals(DuplicateKeyException.class)) {
            return invalidateTokens(ServerResponse.badRequest())
                .contentType(MediaType.APPLICATION_JSON).bodyValue(new CreateUserDeniedResponse());
        }

        return invalidateTokens(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR))
            .contentType(MediaType.APPLICATION_JSON)
            .build();
    }

    private ServerResponse.BodyBuilder invalidateTokens(ServerResponse.BodyBuilder responseBuilder) {
        return responseBuilder;
    }
}

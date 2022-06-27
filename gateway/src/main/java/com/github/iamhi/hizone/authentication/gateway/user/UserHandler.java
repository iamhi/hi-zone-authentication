package com.github.iamhi.hizone.authentication.gateway.user;

import com.github.iamhi.hizone.authentication.api.requests.AddAdminRoleRequest;
import com.github.iamhi.hizone.authentication.api.requests.CreateUserRequest;
import com.github.iamhi.hizone.authentication.api.requests.LoginUserRequest;
import com.github.iamhi.hizone.authentication.api.responses.AddAdminRoleResponse;
import com.github.iamhi.hizone.authentication.api.responses.CreateUserDeniedResponse;
import com.github.iamhi.hizone.authentication.api.responses.LoginDeniedResponse;
import com.github.iamhi.hizone.authentication.api.responses.UserResponse;
import com.github.iamhi.hizone.authentication.config.RedirectConfig;
import com.github.iamhi.hizone.authentication.core.CookieService;
import com.github.iamhi.hizone.authentication.core.UserService;
import com.github.iamhi.hizone.authentication.core.exceptions.UserNotFoundThrowable;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import com.github.iamhi.hizone.authentication.gateway.shared.SharedCookieHelper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URI;

@Component
public record UserHandler(
    UserService userService,
    CookieService cookieService,
    SharedCookieHelper sharedCookieHelper,
    RedirectConfig redirectConfig
) {
    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserRequest.class)
            .flatMap(createUserRequest ->
                Mono.zip(
                    userService.createUser(
                        createUserRequest.username(),
                        createUserRequest.password(),
                        createUserRequest.email()
                    ),
                    Mono.just(createUserRequest.preventRedirect())
                ))
            .flatMap(this::loginSuccessful)
            .onErrorResume(throwable -> this.loginDenied(throwable, serverRequest));
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest
            .formData().flatMap(formData -> formData.isEmpty() ? Mono.empty() : Mono.just(formData))
            .map(LoginUserRequest::fromFormData)
            .switchIfEmpty(serverRequest.bodyToMono(LoginUserRequest.class)).flatMap(
                loginRequest -> Mono.zip(
                    userService.userLogin(loginRequest.username(), loginRequest.password()),
                    Mono.just(loginRequest.preventRedirect())
                ))
            .flatMap(this::loginSuccessful)
            .onErrorResume(throwable -> this.loginDenied(throwable, serverRequest));
    }

    public Mono<ServerResponse> me(ServerRequest serverRequest) {
        return cookieService.getUserFromAccessToken(serverRequest.cookies()).map(userDTO -> new UserResponse(
            userDTO.uuid(),
            userDTO.username(),
            userDTO.email(),
            userDTO.roles().stream().map(Enum::name).toList()
        )).flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse));
    }

    public Mono<ServerResponse> addAdminRole(ServerRequest serverRequest) {
        return Mono.zip(
                serverRequest.bodyToMono(AddAdminRoleRequest.class),
                cookieService.getUserFromAccessToken(serverRequest.cookies())
            ).flatMap(requestTuple ->
                userService.addAdminRole(
                    requestTuple.getT2().username(),
                    requestTuple.getT1().otk(),
                    requestTuple.getT1().buildTimeSecret()))
            .flatMap(userDTO ->
                ServerResponse.ok().bodyValue(new AddAdminRoleResponse(
                    userDTO.roles().stream().map(Enum::name).toList()
                )));
    }

    public Mono<ServerResponse> logout(ServerRequest serverRequest) {
        return invalidateCookies(ServerResponse.ok(), serverRequest)
            .flatMap(ServerResponse.HeadersBuilder::build);
    }

    private Mono<ServerResponse> loginSuccessful(Tuple2<UserDTO, Boolean> tuple2) {
        UserDTO userDTO = tuple2.getT1();

        if (Boolean.TRUE.equals(tuple2.getT2())) {
            return addNewCookies(ServerResponse.ok(), userDTO)
                .flatMap(bodyBuilder -> bodyBuilder
                    .bodyValue(new UserResponse(
                        userDTO.uuid(),
                        userDTO.username(),
                        userDTO.email(),
                        userDTO.roles().stream().map(Enum::name).toList()
                    )));
        }

        return addNewCookies(ServerResponse.seeOther(URI.create(redirectConfig.getUrl())), userDTO)
            .flatMap(bodyBuilder -> bodyBuilder
                .bodyValue(new UserResponse(
                    userDTO.uuid(),
                    userDTO.username(),
                    userDTO.email(),
                    userDTO.roles().stream().map(Enum::name).toList()
                )));
    }

    private Mono<ServerResponse> loginDenied(Throwable throwable, ServerRequest serverRequest) {
        if (throwable.getClass().equals(UserNotFoundThrowable.class)) {
                return invalidateCookies(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON), serverRequest)
                    .flatMap(bodyBuilder -> bodyBuilder.bodyValue(new LoginDeniedResponse()));
        }

        if (throwable.getClass().equals(DuplicateKeyException.class)) {
            return invalidateCookies(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON), serverRequest)
                .flatMap(bodyBuilder -> bodyBuilder.bodyValue(new CreateUserDeniedResponse()));
        }

        System.out.println(throwable.getMessage());
        throwable.printStackTrace();

        return invalidateCookies(
            ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON
                ), serverRequest).flatMap(ServerResponse.BodyBuilder::build);
    }

    public Mono<ServerResponse> refreshAccessToken(ServerRequest serverRequest) {
        return addNewAccessCookie(ServerResponse.ok(), serverRequest).flatMap(ServerResponse.BodyBuilder::build);
    }

    private Mono<ServerResponse.BodyBuilder> addNewAccessCookie(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest) {
        return sharedCookieHelper.refreshAccessToken(responseBuilder, serverRequest);
    }

    private Mono<ServerResponse.BodyBuilder> addNewCookies(ServerResponse.BodyBuilder responseBuilder, UserDTO userDTO) {
        return sharedCookieHelper.addNewCookies(responseBuilder, userDTO);
    }

    private Mono<ServerResponse.BodyBuilder> invalidateCookies(ServerResponse.BodyBuilder responseBuilder, ServerRequest serverRequest) {
        return sharedCookieHelper.invalidateCookies(responseBuilder, serverRequest);
    }
}

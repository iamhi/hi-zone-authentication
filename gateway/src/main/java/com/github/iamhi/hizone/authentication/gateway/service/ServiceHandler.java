package com.github.iamhi.hizone.authentication.gateway.service;

import com.github.iamhi.hizone.authentication.api.requests.LoginUserRequest;
import com.github.iamhi.hizone.authentication.api.requests.ServiceUserInfoRequest;
import com.github.iamhi.hizone.authentication.api.responses.ServiceLoginResponse;
import com.github.iamhi.hizone.authentication.api.responses.UserResponse;
import com.github.iamhi.hizone.authentication.core.TokenService;
import com.github.iamhi.hizone.authentication.core.UserService;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import com.github.iamhi.hizone.authentication.core.models.UserRoleEnum;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public record ServiceHandler(
    UserService userService,
    TokenService tokenService
) {

    Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest
            .bodyToMono(LoginUserRequest.class)
            .flatMap(loginUserRequest -> userService.userLogin(loginUserRequest.username(), loginUserRequest.password()))
            .flatMap(this::userIsService)
            .flatMap(userDto ->
                tokenService.createToken(tokenService.getRefreshTokenPayload(userDto), TokenService.ACCESS_TOKEN_EXPIRATION)
            ).map(ServiceLoginResponse::new).flatMap(serviceLoginResponse ->
                ServerResponse.ok().bodyValue(serviceLoginResponse)
            );
    }

    Mono<ServerResponse> getUserInfo(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ServiceUserInfoRequest.class)
            .flatMap(serviceRequest -> Mono.zip(
                getIfService(serviceRequest.token()),
                Mono.just(serviceRequest.uuid())
            ))
            .flatMap(requestTuple -> userService.findUser(requestTuple.getT2()))
            .flatMap(userDTO -> ServerResponse.ok().bodyValue(
                new UserResponse(
                    userDTO.uuid(),
                    userDTO.username(),
                    userDTO.email(),
                    userDTO.roles().stream().map(Enum::name).toList()
                )
            ));
    }

    Mono<UserDTO> getIfService(String token) {
        return tokenService.decodeToken(token)
            .map(claims -> claims.get(TokenService.USER_UUID, String.class))
            .flatMap(userService::findUser)
            .flatMap(this::userIsService);
    }

    Mono<UserDTO> userIsService(UserDTO userDTO) {
        return userDTO.roles().contains(UserRoleEnum.SERVICE) ?
            Mono.just(userDTO) : Mono.error(new RuntimeException());
    }
}

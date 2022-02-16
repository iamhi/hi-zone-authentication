package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDTO> createUser(String username, String password, String email);

    Mono<UserDTO> findUser(String uuid);

    Mono<UserDTO> userLogin(String username, String password);

    Mono<UserDTO> addAdminRole(String username, String otk, String buildTimeSecret);

    Mono<Boolean> hasRole(String username, String role);
}

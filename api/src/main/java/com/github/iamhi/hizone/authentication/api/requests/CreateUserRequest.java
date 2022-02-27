package com.github.iamhi.hizone.authentication.api.requests;

import java.util.Objects;

public record CreateUserRequest(
    String username,
    String password,
    String email,
    boolean preventRedirect
) {

    public CreateUserRequest {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(email);
    }
}
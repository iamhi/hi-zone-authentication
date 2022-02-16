package com.github.iamhi.hizone.authentication.api.requests;

import java.util.Objects;

public record LoginUserRequest(
    String username,
    String password
) {
    public LoginUserRequest {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }
}

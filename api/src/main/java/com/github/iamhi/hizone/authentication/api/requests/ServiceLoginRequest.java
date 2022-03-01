package com.github.iamhi.hizone.authentication.api.requests;

public record ServiceLoginRequest(
    String username,
    String password
) {
}

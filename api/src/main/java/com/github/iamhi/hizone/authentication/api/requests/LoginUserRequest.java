package com.github.iamhi.hizone.authentication.api.requests;

import org.springframework.util.MultiValueMap;

import java.util.Objects;

public record LoginUserRequest(
    String username,
    String password
) {
    public LoginUserRequest {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }

    public static LoginUserRequest fromFormData(MultiValueMap<String, String> formData) {
        return new LoginUserRequest(formData.getFirst("username-input"), formData.getFirst("password-input"));
    }
}

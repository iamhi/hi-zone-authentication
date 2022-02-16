package com.github.iamhi.hizone.authentication.api.responses;

public record UserResponse(
    String uuid,

    String username,

    String email
) {}

package com.github.iamhi.hizone.authentication.api.responses;

import java.util.List;

public record UserResponse(
    String uuid,

    String username,

    String email,

    List<String> roles
) {}

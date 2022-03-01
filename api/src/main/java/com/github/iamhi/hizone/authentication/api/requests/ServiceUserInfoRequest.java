package com.github.iamhi.hizone.authentication.api.requests;

public record ServiceUserInfoRequest(
    String token,
    String uuid
) {
}

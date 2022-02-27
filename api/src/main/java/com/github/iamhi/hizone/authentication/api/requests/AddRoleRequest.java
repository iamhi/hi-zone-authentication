package com.github.iamhi.hizone.authentication.api.requests;

public record AddRoleRequest(
    String username,
    String role
) {
}

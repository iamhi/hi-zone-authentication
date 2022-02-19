package com.github.iamhi.hizone.authentication.api.requests;

public record AddAdminRoleRequest(
    String otk,
    String buildTimeSecret
) {
}

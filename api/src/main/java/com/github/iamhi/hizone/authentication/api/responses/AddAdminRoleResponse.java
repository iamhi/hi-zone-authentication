package com.github.iamhi.hizone.authentication.api.responses;

import java.util.List;

public record AddAdminRoleResponse(
    List<String> roles
) {
}

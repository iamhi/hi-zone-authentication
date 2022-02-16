package com.github.iamhi.hizone.authentication.core.models;

import com.github.iamhi.hizone.authentication.data.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public record UserDTO(
    String uuid,

    String username,

    String email,

    List<UserRoleEnum> roles
) {
    public static UserDTO fromEntity(UserEntity userEntity) {
        return new UserDTO(
            userEntity.uuid(),
            userEntity.username(),
            userEntity.email(),
            userEntity.roles().stream().map(UserRoleEnum::valueOf).toList()
        );
    }
}

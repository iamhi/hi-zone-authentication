package com.github.iamhi.hizone.authentication.core.models;

import com.github.iamhi.hizone.authentication.data.UserEntity;

import java.util.ArrayList;
import java.util.List;

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
            new ArrayList<>() // TODO
//            userEntity.roles().stream().map(UserRoleEnum::valueOf).toList()
        );
    }

    public UserDTO addRole(UserRoleEnum role) {
        List<UserRoleEnum> newRoles = this.roles();

        newRoles.add(role);

        return new UserDTO(
            this.uuid,
            this.username,
            this.email,
            newRoles
        );
    }

    public UserDTO setRoles(List<UserRoleEnum> roles) {
        return new UserDTO(
            this.uuid,
            this.username,
            this.email,
            roles
        );
    }
}

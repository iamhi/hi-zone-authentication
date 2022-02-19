package com.github.iamhi.hizone.authentication.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record UserEntity(
    @Id
    String uuid,

    @Indexed(name = "user_entity_username_index", unique = true)
    String username,

    String password,

    @Indexed(name = "user_entity_email_index", unique = true, sparse = true)
    String email,

    List<String> roles
) {
    public UserEntity addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }

        return new UserEntity(
            uuid,
            username,
            password,
            email,
            roles
        );
    }
}

package com.github.iamhi.hizone.authentication.data;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
public record UserEntity(
    @Column("uuid")
    String uuid,

    @Column("username")
    String username,

    @Column("password")
    String password,

    @Column("email")
    String email
) {
}

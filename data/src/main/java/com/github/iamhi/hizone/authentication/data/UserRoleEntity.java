package com.github.iamhi.hizone.authentication.data;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
public record UserRoleEntity(
    @Column("uuid")
    String uuid,

    @Column("name")
    String name,

    @Column("username")
    String username
) {
}

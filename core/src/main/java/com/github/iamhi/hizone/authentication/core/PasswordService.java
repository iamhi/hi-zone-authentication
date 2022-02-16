package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.data.UserEntity;
import reactor.core.publisher.Mono;

public interface PasswordService {

    String encryptPassword(String password);

    boolean compare(String originalPassword, String encryptedPassword);

    Mono<UserEntity> comparePasswords(UserEntity userEntity, String password);
}

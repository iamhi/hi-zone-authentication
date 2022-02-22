package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.core.exceptions.UserNotFoundThrowable;
import com.github.iamhi.hizone.authentication.data.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
record PasswordServiceImpl(
    BCryptPasswordEncoder bCryptPasswordEncoder
) implements PasswordService {

    PasswordServiceImpl() {
        this(new BCryptPasswordEncoder());
    }

    @Override
    public String encryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean compare(String originalPassword, String encryptedPassword) {
        return bCryptPasswordEncoder.matches(originalPassword, encryptedPassword);
    }

    @Override
    public Mono<UserEntity> comparePasswords(UserEntity userEntity, String password) {
        if (compare(password, userEntity.password())) {
            return Mono.just(userEntity);
        }

        return Mono.error(new UserNotFoundThrowable());
    }
}

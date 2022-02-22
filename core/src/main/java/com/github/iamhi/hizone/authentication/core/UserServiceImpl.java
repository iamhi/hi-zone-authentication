package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.AdminConfig;
import com.github.iamhi.hizone.authentication.core.exceptions.InvalidAdminCodesThrowable;
import com.github.iamhi.hizone.authentication.core.exceptions.UserNotFoundThrowable;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import com.github.iamhi.hizone.authentication.core.models.UserRoleEnum;
import com.github.iamhi.hizone.authentication.data.UserEntity;
import com.github.iamhi.hizone.authentication.data.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.UUID;

@Service
record UserServiceImpl(
    UserRepository userRepository,
    PasswordService passwordService,
    AdminConfig adminConfig
) implements UserService {

    @Override
    public Mono<UserDTO> createUser(String username, String password, String email) {
        return userRepository.save(new UserEntity(
            UUID.randomUUID().toString(),
            username,
            passwordService.encryptPassword(password),
            email,
            List.of(UserRoleEnum.BASIC.name())
        )).map(UserDTO::fromEntity);
    }

    @Override
    public Mono<UserDTO> findUser(String uuid) {
        return userRepository.findById(uuid).map(UserDTO::fromEntity);
    }

    @Override
    public Mono<UserDTO> userLogin(String username, String password) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new UserNotFoundThrowable()))
            .flatMap(userEntity -> passwordService.comparePasswords(userEntity, password))
            .map(UserDTO::fromEntity);
    }

    @Override
    public Mono<UserDTO> addAdminRole(String username, String otk, String buildTimeSecret) {
        return Mono.zip(
            isValidOtk(otk),
            isValidBuildTimeSecret(buildTimeSecret),
            Mono.just(username)
        ).flatMap(this::validateAndAddAdminRole);
    }

    @Override
    public Mono<Boolean> hasRole(String username, String role) {
        return userRepository().findByUsername(username).map(UserEntity::roles).map(roles -> roles.contains(role));
    }

    private Mono<Boolean> isValidOtk(String otk) {
        // TOO lazy to implement this yet :D
        return Mono.just(true);
    }

    private Mono<Boolean> isValidBuildTimeSecret(String buildTimeSecret) {
        return Mono.just(adminConfig.getServiceSecret().equals(buildTimeSecret));
    }

    private Mono<UserDTO> validateAndAddAdminRole(Tuple3<Boolean, Boolean, String> tuple3) {
        if (tuple3.getT1() && tuple3.getT2()) {
            return userRepository
                .findByUsername(tuple3.getT3())
                .map(userEntity -> userEntity.addRole(UserRoleEnum.ADMIN.name()))
                .flatMap(userRepository::save)
                .map(UserDTO::fromEntity);
        }

        return Mono.error(new InvalidAdminCodesThrowable());
    }
}

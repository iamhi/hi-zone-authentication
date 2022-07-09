package com.github.iamhi.hizone.authentication.core;

import com.github.iamhi.hizone.authentication.config.AdminConfig;
import com.github.iamhi.hizone.authentication.core.exceptions.InvalidAdminCodesThrowable;
import com.github.iamhi.hizone.authentication.core.exceptions.UserNotFoundThrowable;
import com.github.iamhi.hizone.authentication.core.models.UserDTO;
import com.github.iamhi.hizone.authentication.core.models.UserRoleEnum;
import com.github.iamhi.hizone.authentication.data.UserEntity;
import com.github.iamhi.hizone.authentication.data.UserRepository;
import com.github.iamhi.hizone.authentication.data.UserRoleEntity;
import com.github.iamhi.hizone.authentication.data.UserRoleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.Objects;
import java.util.UUID;

@Service
record UserServiceImpl(
    UserRepository userRepository,
    UserRoleRepository userRoleRepository,
    PasswordService passwordService,
    AdminConfig adminConfig
) implements UserService {

    @Override
    public Mono<UserDTO> createUser(String username, String password, String email) {
        return userRepository.save(new UserEntity(
            UUID.randomUUID().toString(),
            username,
            passwordService.encryptPassword(password),
            email
        )).map(UserDTO::fromEntity).flatMap(userDTO -> addRole(userDTO, UserRoleEnum.BASIC));
    }

    @Override
    public Mono<UserDTO> findUser(String uuid) {
        return userRepository.findByUuid(uuid).map(UserDTO::fromEntity).flatMap(this::populateRoles);
    }

    @Override
    public Mono<UserDTO> userLogin(String username, String password) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new UserNotFoundThrowable()))
            .flatMap(userEntity -> passwordService.comparePasswords(userEntity, password))
            .map(UserDTO::fromEntity).flatMap(this::populateRoles);
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
        return
            userRoleRepository.findByUsername(username).any(userRoleEntity -> userRoleEntity.name().equals(role));
    }

    @Override
    public Mono<Boolean> addRole(String username, String role) {
        UserRoleEntity userRoleEntity = new UserRoleEntity(
            UUID.randomUUID().toString(),
            role,
            username
        );

        return userRoleRepository.save(userRoleEntity).map(Objects::nonNull);
    }

    private Mono<UserDTO> addRole(UserDTO userDTO, UserRoleEnum userRole) {
        return addRole(userDTO.username(), userRole.name()).map(result -> Boolean.TRUE.equals(result)
            ? userDTO.addRole(userRole) : userDTO);
    }

    private Mono<Boolean> isValidOtk(String otk) {
        // TOO lazy to implement this yet :D
        return Mono.just(true);
    }

    private Mono<Boolean> isValidBuildTimeSecret(String buildTimeSecret) {
        return Mono.just(adminConfig.getServiceSecret().equals(buildTimeSecret));
    }

    private Mono<UserDTO> populateRoles(UserDTO userDTO) {
        return userRoleRepository.findByUsername(userDTO.username())
            .map(UserRoleEntity::name)
            .distinct()
            .map(UserRoleEnum::valueOf)
            .collectList().map(userDTO::setRoles);
    }

    private Mono<UserDTO> validateAndAddAdminRole(Tuple3<Boolean, Boolean, String> tuple3) {
        if (tuple3.getT1() && tuple3.getT2()) {
            return userRepository
                .findByUsername(tuple3.getT3())
                .map(UserDTO::fromEntity)
                .flatMap(userDTO -> addRole(userDTO, UserRoleEnum.ADMIN));
        }

        return Mono.error(new InvalidAdminCodesThrowable());
    }
}

package com.github.iamhi.hizone.authentication.api.responses;

public record CreateUserDeniedResponse(String errorCode, String message) {
    public CreateUserDeniedResponse(){
        this(
            "2100",
            "Unable to create user with that username"
        );
    }
}

package com.github.iamhi.hizone.authentication.api.responses;

public record LoginDeniedResponse(String errorCode, String message) {
    public LoginDeniedResponse() {
        this("2056", "Wrong credentials or user doesn't exist");
    }
}


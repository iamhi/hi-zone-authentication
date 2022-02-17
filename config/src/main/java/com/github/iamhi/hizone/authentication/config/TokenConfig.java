package com.github.iamhi.hizone.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("authentication.service.admin")
@ConstructorBinding
@Data
public class TokenConfig {

    private String tokenSecret;

    private long accessTokenLife;

    private long refreshTokenLife;
}

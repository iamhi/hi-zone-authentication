package com.github.iamhi.hizone.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("authentication.service.cookie")
@Data
public class CookieConfig {

    private String accessTokenPath;

    private String refreshTokenPath;
}

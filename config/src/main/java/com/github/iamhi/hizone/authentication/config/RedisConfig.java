package com.github.iamhi.hizone.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.redis")
@Data
public class RedisConfig {

    private int database;

    private String host;

    private int port;

    private String password;

    private int timeout;
}

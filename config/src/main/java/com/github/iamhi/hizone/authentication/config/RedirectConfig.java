package com.github.iamhi.hizone.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("authentication.user.flow.redirect")
@Data
public class RedirectConfig {

    private String url;
}

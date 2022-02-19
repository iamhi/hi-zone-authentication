package com.github.iamhi.hizone.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authentication.service.admin")
//@ConstructorBinding
@Data
public class AdminConfig {

    String serviceSecret;
}

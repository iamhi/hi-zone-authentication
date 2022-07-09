package com.github.iamhi.hizone.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = {"com.github.iamhi.hizone.authentication"})
//@EnableJpaRepositories(basePackages = {"com.github.iamhi.hizone.authentication.data"})
@ConfigurationPropertiesScan("com.github.iamhi.hizone.authentication.config")
@EnableR2dbcRepositories(basePackages = {"com.github.iamhi.hizone.authentication.data"})
public class AuthenticationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}

package com.github.iamhi.hizone.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.github.iamhi.hizone.authentication"})
@EnableReactiveMongoRepositories("com.github.iamhi.hizone.authentication.data")
@ConfigurationPropertiesScan("com.github.iamhi.hizone.authentication.config")
public class AuthenticationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}

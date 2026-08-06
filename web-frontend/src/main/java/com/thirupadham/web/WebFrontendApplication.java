package com.thirupadham.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebFrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebFrontendApplication.class, args);
    }
}

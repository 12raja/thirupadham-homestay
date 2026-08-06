package com.thirupadham.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is the entry point of the whole service - the "main" method here is
// what actually starts the embedded web server (Tomcat, bundled inside
// Spring Boot) when you run this jar.
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

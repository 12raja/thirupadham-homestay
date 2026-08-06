package com.thirupadham.booking;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate is what lets this service make an outgoing HTTP call to
    // another service (notification-service) - this is the actual mechanism
    // behind "service-to-service communication" you'll see used below.
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

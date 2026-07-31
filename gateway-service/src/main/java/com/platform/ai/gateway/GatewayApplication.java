// Main entry point for the Java 21 Spring Boot Edge API Gateway.
// Bootstraps the embedded web server and initializes application context.

package com.platform.ai.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

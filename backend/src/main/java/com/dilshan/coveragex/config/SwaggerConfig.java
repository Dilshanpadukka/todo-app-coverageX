package com.dilshan.coveragex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo/Task Management API")
                        .description("A comprehensive REST API for managing tasks and todos. " +
                                   "This API provides endpoints for creating, reading, updating, and deleting tasks, " +
                                   "along with filtering, searching, and statistics capabilities.")
                        .version("1.0.0")
                );
    }
}

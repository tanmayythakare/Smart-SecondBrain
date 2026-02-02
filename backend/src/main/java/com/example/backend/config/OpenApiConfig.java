package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI secondBrainOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Life OS / Second Brain API")
                        .description("API documentation for the Life OS productivity system")
                        .version("v1.0"));
    }
}

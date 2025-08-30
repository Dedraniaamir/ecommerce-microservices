package com.msproj.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI libraryApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service Management")
                        .version("1.0")
                        .description("API for User Service Management"));
    }
}

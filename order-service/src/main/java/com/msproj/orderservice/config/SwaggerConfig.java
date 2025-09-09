package com.msproj.orderservice.config;

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
                        .title("Order Service Management")
                        .version("1.0")
                        .description("API for Order Service Management"));
    }
}

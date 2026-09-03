package com.portfoliohub.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI portfolioHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PortfolioHub API")
                        .version("v1")
                        .description("Backend API for PortfolioHub")
                        .contact(new Contact().name("PortfolioHub")));
    }
}

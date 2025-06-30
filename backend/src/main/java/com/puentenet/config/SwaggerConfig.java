package com.puentenet.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Puentenet Market Monitor API")
                        .description("API para monitorear instrumentos financieros (acciones y criptomonedas) con datos en tiempo real")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Puentenet Team")
                                .email("support@puentenet.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8092/api")
                                .description("Development server"),
                        new Server()
                                .url("/api")
                                .description("Default server")
                ));
    }
} 
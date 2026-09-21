package com.example.demospringpedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo Spring Pedidos API")
                        .version("v1")
                        .description("API REST para consulta e gerenciamento de usuários, categorias, produtos e pedidos.")
                        .contact(new Contact()
                                .name("Equipe Demo Spring Pedidos")));
    }
}

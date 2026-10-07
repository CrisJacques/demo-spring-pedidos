package com.example.demospringpedidos.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenApiConfigTest {
    @Test
    void customOpenApiContainsExpectedMetadata() {
        var openApi = new OpenApiConfig().customOpenAPI();

        assertEquals("Demo Spring Pedidos API", openApi.getInfo().getTitle());
        assertEquals("v1", openApi.getInfo().getVersion());
        assertEquals("API REST para consulta e gerenciamento de usuários, categorias, produtos e pedidos.",
                openApi.getInfo().getDescription());
        assertEquals("Equipe Demo Spring Pedidos", openApi.getInfo().getContact().getName());
    }
}

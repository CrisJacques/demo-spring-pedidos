package com.example.demospringpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dados permitidos para a criação de um pedido")
public record OrderRequestDto(
        @NotNull(message = "Client id is required.")
        @Schema(description = "ID do cliente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long clientId,

        @NotEmpty(message = "At least one item is required.")
        @Schema(description = "Produtos e quantidades do pedido", requiredMode = Schema.RequiredMode.REQUIRED)
        List<@NotNull(message = "Order items cannot be null.") @Valid OrderItemRequestDto> items) {
}

package com.example.demospringpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Produto e quantidade solicitados para o pedido")
public record OrderItemRequestDto(
        @NotNull(message = "Product id is required for each item.")
        @Schema(description = "ID do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long productId,

        @NotNull(message = "Item quantity must be greater than zero.")
        @Positive(message = "Item quantity must be greater than zero.")
        @Schema(description = "Quantidade", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer quantity) {
}

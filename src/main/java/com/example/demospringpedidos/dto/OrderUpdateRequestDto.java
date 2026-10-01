package com.example.demospringpedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Dados permitidos para a atualização de um pedido")
public record OrderUpdateRequestDto(
        @NotNull(message = "Order status is required.")
        @Schema(description = "Status do pedido: 1 = Aguardando pagamento, 2 = Pago, 3 = Enviado, "
                + "4 = Entregue, 5 = Cancelado.",
                example = "1",
                allowableValues = {"1", "2", "3", "4", "5"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        int orderStatus,

        @NotEmpty(message = "At least one item is required.")
        @Schema(description = "Produtos e quantidades do pedido", requiredMode = Schema.RequiredMode.REQUIRED)
        List<@NotNull(message = "Order items cannot be null.") @Valid OrderItemRequestDto> items) {
}

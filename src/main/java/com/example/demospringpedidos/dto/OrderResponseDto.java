package com.example.demospringpedidos.dto;

import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Payment;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Dados do pedido retornados pela API")
public record OrderResponseDto(
        @Schema(description = "Identificador único do pedido", example = "1")
        Long id,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
        @Schema(description = "Instante de criação do pedido", example = "2019-06-20T19:53:07Z")
        Instant moment,

        @Schema(description = "Status atual do pedido", example = "PAID",
                allowableValues = {"WAITING_PAYMENT", "PAID", "SHIPPED", "DELIVERED", "CANCELED"})
        OrderStatus orderStatus,

        @Schema(implementation = UserResponseDto.class)
        UserResponseDto client,

        @Schema(description = "Lista de itens do pedido, com produto e quantidade")
        List<OrderItem> items,

        @Schema(description = "Dados de pagamento do pedido")
        Payment payment,

        @Schema(description = "Valor total do pedido")
        Double total) {
}

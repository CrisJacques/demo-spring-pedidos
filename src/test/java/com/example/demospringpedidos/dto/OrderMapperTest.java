package com.example.demospringpedidos.dto;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {
    private final OrderMapper mapper = new OrderMapper(new UserMapper());

    @Test
    void mapsOrderClientToPasswordFreeUserResponse() {
        User client = new User(1L, "Maria", "maria@example.com", "999999999", "Abcdefg1");
        Order order = new Order(2L, Instant.parse("2024-01-01T00:00:00Z"), OrderStatus.PAID, client);

        OrderResponseDto response = mapper.toResponse(order);

        assertEquals(new UserResponseDto(1L, "Maria", "maria@example.com", "999999999"), response.client());
    }
}

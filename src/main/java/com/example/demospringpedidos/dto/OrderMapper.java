package com.example.demospringpedidos.dto;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final UserMapper userMapper;

    public OrderResponseDto toResponse(Order order) {
        User client = order.getClient();
        if (client == null) {
            throw new IllegalStateException("Cannot map an order without a client.");
        }

        return new OrderResponseDto(
                order.getId(),
                order.getMoment(),
                order.getOrderStatus(),
                userMapper.toResponse(client),
                order.getItems(),
                order.getPayment(),
                order.getTotal());
    }
}

package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.dto.OrderItemRequestDto;
import com.example.demospringpedidos.dto.OrderMapper;
import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.dto.OrderResponseDto;
import com.example.demospringpedidos.services.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {

    @Mock OrderService orderService;
    @Mock OrderMapper orderMapper;
    @InjectMocks OrderResource orderResource;

    @AfterEach void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }


    @Test void orderResourceReturnsListAndItem() {
        List<Order> list = List.of(new Order());
        OrderResponseDto response = new OrderResponseDto(1L, null, null, null, List.of(), null, 0.0);
        when(orderService.findAll()).thenReturn(list);
        when(orderService.findById(1L)).thenReturn(list.get(0));
        when(orderMapper.toResponse(list.get(0))).thenReturn(response);
        assertEquals(List.of(response), orderResource.findAll().getBody());
        assertSame(response, orderResource.findById(1L).getBody());
    }

    @Test void orderResourceInsertReturnsCreatedResourceAndLocation() {
        Order order = new Order();
        order.setId(42L);
        OrderResponseDto responseDto = new OrderResponseDto(42L, null, null, null, List.of(), null, 0.0);
        OrderRequestDto requestDto = new OrderRequestDto(1L, List.of(new OrderItemRequestDto(3L, 2)));
        when(orderService.insert(requestDto)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(responseDto);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/orders");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = orderResource.insert(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/orders/42", response.getHeaders().getLocation().getPath());
        assertSame(responseDto, response.getBody());
        verify(orderService).insert(requestDto);
    }

}

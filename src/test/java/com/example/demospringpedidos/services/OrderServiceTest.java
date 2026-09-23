package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository repository;
    @InjectMocks OrderService service;

    @Test void findAllReturnsRepositoryData() {
        List<Order> orders = List.of(new Order());
        when(repository.findAll()).thenReturn(orders);
        assertSame(orders, service.findAll());
    }

    @Test void findByIdReturnsOrderWhenFound() {
        Order order = new Order();
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        assertSame(order, service.findById(1L));
    }

    @Test void findByIdThrowsWhenOrderIsMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById(1L));
    }
}

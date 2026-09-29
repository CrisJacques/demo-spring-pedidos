package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderItemRepository;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository repository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock UserService userService;
    @Mock ProductService productService;
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

    @Test void insertUsesStoredClientAndProductDataAndSetsServerOwnedFields() {
        User client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "Abcdefg1");
        Product product = new Product(1L, "The Lord of the Rings", "Description", 90.5, "");
        Order request = new Order(999L, Instant.parse("2000-01-01T00:00:00Z"), OrderStatus.PAID,
                new User(1L, "Untrusted", "untrusted@example.com", "0", "Abcdefg1"));
        request.getItems().add(new OrderItem(request, new Product(1L, null, null, -1.0, ""), 2, 0.01));
        when(userService.findById(1L)).thenReturn(client);
        when(productService.findAllById(Set.of(1L))).thenReturn(List.of(product));
        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        Order result = service.insert(request);

        assertEquals(42L, result.getId());
        assertSame(client, result.getClient());
        assertNotEquals(Instant.parse("2000-01-01T00:00:00Z"), result.getMoment());
        assertEquals(OrderStatus.WAITING_PAYMENT, result.getOrderStatus());
        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().iterator().next();
        assertSame(result, item.getOrder());
        assertSame(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(90.5, item.getPrice());
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test void insertLoadsProductsInOneBatch() {
        User client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "Abcdefg1");
        Product book = new Product(1L, "Book", "Description", 10.0, "");
        Product pen = new Product(2L, "Pen", "Description", 2.0, "");
        Order request = new Order(null, null, null, new User(1L, null, null, null, null));
        request.getItems().add(new OrderItem(request, new Product(1L, null, null, null, null), 2, null));
        request.getItems().add(new OrderItem(request, new Product(2L, null, null, null, null), 1, null));
        when(userService.findById(1L)).thenReturn(client);
        when(productService.findAllById(Set.of(1L, 2L))).thenReturn(List.of(book, pen));
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = service.insert(request);

        assertEquals(2, result.getItems().size());
        verify(productService, times(1)).findAllById(Set.of(1L, 2L));
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test void insertRejectsMissingClientId() {
        Order request = new Order();

        BusinessException exception = assertThrows(BusinessException.class, () -> service.insert(request));

        assertEquals("Client id is required.", exception.getMessage());
        verifyNoInteractions(userService, productService, repository, orderItemRepository);
    }

    @Test void insertRejectsNullRequestAndNonPositiveQuantity() {
        BusinessException missingRequest = assertThrows(BusinessException.class, () -> service.insert(null));
        assertEquals("Order body is required.", missingRequest.getMessage());

        Order request = new Order(null, null, null, new User(1L, null, null, null, null));
        request.getItems().add(new OrderItem(request, new Product(1L, null, null, null, null), 0, null));
        when(userService.findById(1L)).thenReturn(new User(1L, "Maria", "maria@example.com", "999", "Abcdefg1"));

        BusinessException invalidQuantity = assertThrows(BusinessException.class, () -> service.insert(request));
        assertEquals("Item quantity must be greater than zero.", invalidQuantity.getMessage());
        verify(repository, never()).save(any(Order.class));
    }

    @Test void insertRejectsMissingProductWithoutSavingOrder() {
        Order request = new Order(null, null, null, new User(1L, null, null, null, null));
        request.getItems().add(new OrderItem(request, new Product(), 1, 1.0));
        when(userService.findById(1L)).thenReturn(new User(1L, "Maria", "maria@example.com", "999", "Abcdefg1"));

        assertThrows(BusinessException.class, () -> service.insert(request));

        verify(repository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository);
    }

    @Test void insertPropagatesMissingClientOrProduct() {
        Order request = new Order(null, null, null, new User(99L, null, null, null, null));
        request.getItems().add(new OrderItem(request, new Product(1L, null, null, null, null), 1, null));
        when(userService.findById(99L)).thenThrow(new ResourceNotFoundException(99L));

        assertThrows(ResourceNotFoundException.class, () -> service.insert(request));
        verify(repository, never()).save(any(Order.class));

        doReturn(new User(99L, "Client", "client@example.com", "999", "Abcdefg1"))
                .when(userService).findById(99L);
        when(productService.findAllById(Set.of(1L))).thenThrow(new ResourceNotFoundException(1L));
        assertThrows(ResourceNotFoundException.class, () -> service.insert(request));
        verify(repository, never()).save(any(Order.class));
    }
}

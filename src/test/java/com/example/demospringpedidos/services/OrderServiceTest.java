package com.example.demospringpedidos.services;

import com.example.demospringpedidos.dto.OrderItemRequestDto;
import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.dto.OrderUpdateRequestDto;
import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Payment;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock private OrderRepository repository;
    @Mock private UserService userService;
    @Mock private ProductService productService;
    @Spy private Clock clock = Clock.fixed(Instant.parse("2026-09-29T12:00:00Z"), ZoneOffset.UTC);
    @InjectMocks private OrderService service;

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
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test void deleteRemovesExistingOrder() {
        Order order = new Order();
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test void deleteDoesNotRemoveOrderWhenItDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));

        verify(repository).findById(9L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test void insertUsesStoredClientAndProductDataAndSetsServerOwnedFields() {
        User client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "Abcdefg1");
        Product product = new Product(1L, "The Lord of the Rings", "Description", 90.5, "");
        OrderRequestDto request = new OrderRequestDto(1L, List.of(new OrderItemRequestDto(1L, 2)));
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
        assertEquals(Instant.parse("2026-09-29T12:00:00Z"), result.getMoment());
        assertEquals(OrderStatus.WAITING_PAYMENT, result.getOrderStatus());
        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().iterator().next();
        assertSame(result, item.getOrder());
        assertSame(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(90.5, item.getPrice());
        verify(repository).save(any(Order.class));
    }

    @Test void insertAggregatesRepeatedProductsAndLoadsProductsInOneBatch() {
        User client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "Abcdefg1");
        Product book = new Product(1L, "Book", "Description", 10.0, "");
        Product pen = new Product(2L, "Pen", "Description", 2.0, "");
        OrderRequestDto request = new OrderRequestDto(1L, List.of(
                new OrderItemRequestDto(1L, 2),
                new OrderItemRequestDto(1L, 3),
                new OrderItemRequestDto(2L, 1)));
        when(userService.findById(1L)).thenReturn(client);
        when(productService.findAllById(Set.of(1L, 2L))).thenReturn(List.of(book, pen));
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = service.insert(request);

        assertEquals(2, result.getItems().size());
        assertSame(book, result.getItems().get(0).getProduct());
        assertEquals(5, result.getItems().get(0).getQuantity());
        assertSame(pen, result.getItems().get(1).getProduct());
        assertEquals(1, result.getItems().get(1).getQuantity());
        verify(productService, times(1)).findAllById(Set.of(1L, 2L));
    }

    @Test void insertRejectsAggregatedQuantityOverflowBeforeLookingUpEntities() {
        OrderRequestDto request = new OrderRequestDto(1L, List.of(
                new OrderItemRequestDto(1L, Integer.MAX_VALUE),
                new OrderItemRequestDto(1L, 1)));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.insert(request));

        assertEquals("Total quantity for a product exceeds the supported limit.", exception.getMessage());
        verifyNoInteractions(userService, productService, repository);
    }

    @Test void insertRejectsMissingClientId() {
        OrderRequestDto request = new OrderRequestDto(null, List.of(new OrderItemRequestDto(1L, 1)));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.insert(request));

        assertEquals("Client id is required.", exception.getMessage());
        verifyNoInteractions(userService, productService, repository);
    }

    @Test void insertRejectsNullRequestAndNonPositiveQuantity() {
        BusinessException missingRequest = assertThrows(BusinessException.class, () -> service.insert(null));
        assertEquals("Order body is required.", missingRequest.getMessage());

        OrderRequestDto request = new OrderRequestDto(1L, List.of(new OrderItemRequestDto(1L, 0)));
        OrderRequestDto missingQuantity = new OrderRequestDto(1L,
                List.of(new OrderItemRequestDto(1L, null)));

        BusinessException invalidQuantity = assertThrows(BusinessException.class, () -> service.insert(request));
        assertEquals("Item quantity must be greater than zero.", invalidQuantity.getMessage());
        assertEquals("Item quantity must be greater than zero.",
                assertThrows(BusinessException.class, () -> service.insert(missingQuantity)).getMessage());
        verify(repository, never()).save(any(Order.class));
    }

    @Test void insertRejectsMissingProductWithoutSavingOrder() {
        OrderRequestDto request = new OrderRequestDto(1L, List.of(new OrderItemRequestDto(null, 1)));

        assertThrows(BusinessException.class, () -> service.insert(request));

        verify(repository, never()).save(any(Order.class));
    }

    @Test void insertRejectsEmptyItemsAndNullItem() {
        OrderRequestDto nullItems = new OrderRequestDto(1L, null);
        OrderRequestDto emptyItems = new OrderRequestDto(1L, List.of());
        OrderRequestDto nullItem = new OrderRequestDto(1L, java.util.Collections.singletonList(null));

        assertEquals("At least one item is required.",
                assertThrows(BusinessException.class, () -> service.insert(nullItems)).getMessage());
        assertEquals("At least one item is required.",
                assertThrows(BusinessException.class, () -> service.insert(emptyItems)).getMessage());
        assertEquals("Order items cannot be null.",
                assertThrows(BusinessException.class, () -> service.insert(nullItem)).getMessage());
        verifyNoInteractions(userService, productService, repository);
    }

    @Test void insertPropagatesMissingClientOrProduct() {
        OrderRequestDto request = new OrderRequestDto(99L, List.of(new OrderItemRequestDto(1L, 1)));
        when(userService.findById(99L)).thenThrow(new ResourceNotFoundException(99L));

        assertThrows(ResourceNotFoundException.class, () -> service.insert(request));
        verify(repository, never()).save(any(Order.class));

        doReturn(new User(99L, "Client", "client@example.com", "999", "Abcdefg1"))
                .when(userService).findById(99L);
        when(productService.findAllById(Set.of(1L))).thenThrow(new ResourceNotFoundException(1L));
        assertThrows(ResourceNotFoundException.class, () -> service.insert(request));
        verify(repository, never()).save(any(Order.class));
    }

    @Test void updateReconcilesItemsAndCreatesPaymentWhenOrderBecomesPaid() {
        User client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "Abcdefg1");
        Product existingProduct = new Product(1L, "Book", "Description", 12.0, "");
        Product addedProduct = new Product(2L, "Pen", "Description", 2.0, "");
        Product removedProduct = new Product(3L, "Old", "Description", 5.0, "");
        Order order = new Order(10L, Instant.parse("2026-09-28T12:00:00Z"), OrderStatus.WAITING_PAYMENT, client);
        OrderItem existingItem = new OrderItem(order, existingProduct, 1, 10.0);
        order.getItems().add(existingItem);
        order.getItems().add(new OrderItem(order, removedProduct, 3, 5.0));
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(2, List.of(
                new OrderItemRequestDto(1L, 4),
                new OrderItemRequestDto(2L, 2)));
        when(repository.findById(10L)).thenReturn(Optional.of(order));
        when(productService.findAllById(Set.of(1L, 2L))).thenReturn(List.of(
                new Product(1L, "Book", "Description", 15.0, ""),
                addedProduct));
        when(repository.save(order)).thenReturn(order);

        Order result = service.update(10L, request);

        assertSame(order, result);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(existingItem));
        assertEquals(4, existingItem.getQuantity());
        assertEquals(15.0, existingItem.getPrice());
        OrderItem addedItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(2L))
                .findFirst()
                .orElseThrow();
        assertSame(addedProduct, addedItem.getProduct());
        assertEquals(2, addedItem.getQuantity());
        assertTrue(order.getItems().stream().noneMatch(item -> item.getProduct().getId().equals(3L)));
        assertEquals(Instant.parse("2026-09-29T12:00:00Z"), order.getPayment().getMoment());
        assertSame(order, order.getPayment().getOrder());
        verify(repository).save(order);
    }

    @Test void updatePreservesExistingPaymentAndDoesNotCreatePaymentForOtherStatuses() {
        Order paidOrder = new Order(11L, Instant.now(), OrderStatus.PAID, new User());
        Payment existingPayment = new Payment(11L, Instant.parse("2026-09-28T12:00:00Z"), paidOrder);
        paidOrder.setPayment(existingPayment);
        OrderUpdateRequestDto paidRequest = new OrderUpdateRequestDto(2, List.of(new OrderItemRequestDto(1L, 1)));
        when(repository.findById(11L)).thenReturn(Optional.of(paidOrder));
        when(productService.findAllById(Set.of(1L))).thenReturn(List.of(new Product(1L, "Book", "D", 3.0, "")));
        when(repository.save(paidOrder)).thenReturn(paidOrder);

        assertSame(paidOrder, service.update(11L, paidRequest));
        assertSame(existingPayment, paidOrder.getPayment());

        Order shippedOrder = new Order(12L, Instant.now(), OrderStatus.WAITING_PAYMENT, new User());
        OrderUpdateRequestDto shippedRequest = new OrderUpdateRequestDto(3, List.of(new OrderItemRequestDto(1L, 1)));
        when(repository.findById(12L)).thenReturn(Optional.of(shippedOrder));
        when(repository.save(shippedOrder)).thenReturn(shippedOrder);

        assertSame(shippedOrder, service.update(12L, shippedRequest));
        assertEquals(OrderStatus.SHIPPED, shippedOrder.getOrderStatus());
        assertNull(shippedOrder.getPayment());
    }

    @Test void updateRejectsInvalidStatusWithoutSavingOrder() {
        Order order = new Order(13L, Instant.now(), OrderStatus.WAITING_PAYMENT, new User());
        when(repository.findById(13L)).thenReturn(Optional.of(order));
        when(productService.findAllById(Set.of(1L)))
                .thenReturn(List.of(new Product(1L, "Book", "Description", 3.0, "")));
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(99, List.of(new OrderItemRequestDto(1L, 1)));

        assertThrows(IllegalArgumentException.class, () -> service.update(13L, request));

        verify(repository, never()).save(any(Order.class));
    }
}

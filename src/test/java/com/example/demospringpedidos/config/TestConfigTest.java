package com.example.demospringpedidos.config;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.CategoryRepository;
import com.example.demospringpedidos.repositories.OrderItemRepository;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestConfigTest {
    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @InjectMocks private TestConfig testConfig;

    @Test
    void runPersistsSampleDataAndBuildsExpectedAssociations() throws Exception {
        List<Product> savedProducts = new ArrayList<>();
        List<OrderItem> savedItems = new ArrayList<>();
        AtomicLong categoryIds = new AtomicLong(1L);
        doAnswer(invocation -> {
            Iterable<Category> categories = invocation.getArgument(0);
            categories.forEach(category -> category.setId(categoryIds.getAndIncrement()));
            return null;
        }).when(categoryRepository).saveAll(any());
        doAnswer(invocation -> {
            Iterable<Product> products = invocation.getArgument(0);
            products.forEach(savedProducts::add);
            return null;
        }).when(productRepository).saveAll(any());
        doAnswer(invocation -> {
            Iterable<OrderItem> items = invocation.getArgument(0);
            items.forEach(savedItems::add);
            return null;
        }).when(orderItemRepository).saveAll(any());

        testConfig.run();

        verify(userRepository).saveAll(argThat(users -> count(users) == 2));
        verify(orderRepository).saveAll(argThat(orders -> count(orders) == 3));
        verify(categoryRepository).saveAll(argThat(categories -> count(categories) == 3));
        verify(productRepository, times(2)).saveAll(argThat(products -> count(products) == 5));
        verify(orderItemRepository).saveAll(argThat(items -> count(items) == 4));
        verify(orderRepository).save(argThat(order -> order.getOrderStatus() == OrderStatus.PAID
                && order.getPayment() != null));

        Product book = savedProducts.stream()
                .filter(product -> product.getName().equals("The Lord of the Rings"))
                .findFirst()
                .orElseThrow();
        Product television = savedProducts.stream()
                .filter(product -> product.getName().equals("Smart TV"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, book.getCategories().size());
        assertEquals("Books", book.getCategories().iterator().next().getName());
        assertEquals(2, television.getCategories().size());

        OrderItem bookOrderItem = savedItems.stream()
                .filter(item -> item.getProduct().getName().equals("The Lord of the Rings"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, bookOrderItem.getQuantity());
        assertEquals(OrderStatus.PAID, bookOrderItem.getOrder().getOrderStatus());
        assertNotNull(bookOrderItem.getOrder().getPayment());
        assertSame(bookOrderItem.getOrder(), bookOrderItem.getOrder().getPayment().getOrder());
    }

    private long count(Iterable<?> values) {
        return StreamSupport.stream(values.spliterator(), false).count();
    }
}

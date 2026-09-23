package com.example.demospringpedidos.entities;

import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.entities.pk.OrderItemPk;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderDomainTest {
    @Test void orderStatusMapsCodesAndRejectsInvalidCode() {
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf(2));
        assertEquals(2, OrderStatus.PAID.getCode());
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf(99));
    }

    @Test void orderCalculatesTotalFromItems() {
        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, new User());
        order.getItems().add(new OrderItem(order, new Product(1L, "A", "A", 1.0, ""), 2, 10.5));
        order.getItems().add(new OrderItem(order, new Product(2L, "B", "B", 1.0, ""), 1, 5.0));
        assertEquals(26.0, order.getTotal());
    }

    @Test     void emptyOrderHasZeroTotalAndNullStatusCanBeAssigned() {
        Order order = new Order();
        order.setOrderStatus(null);
        assertEquals(0.0, order.getTotal());
    }

    @Test void orderItemCalculatesSubtotalAndExposesCompositeKeyParts() {
        Order order = new Order();
        Product product = new Product(1L, "A", "A", 1.0, "");
        OrderItem item = new OrderItem(order, product, 3, 12.5);
        assertSame(order, item.getOrder());
        assertSame(product, item.getProduct());
        assertEquals(37.5, item.getSubTotal());
        item.setQuantity(4);
        item.setPrice(2.5);
        assertEquals(10.0, item.getSubTotal());
    }

    @Test void productReturnsDistinctOrdersFromItems() {
        Product product = new Product();
        Order first = new Order(1L, Instant.now(), OrderStatus.PAID, null);
        Order second = new Order(2L, Instant.now(), OrderStatus.PAID, null);
        ReflectionTestUtils.setField(product, "items", Set.of(
                new OrderItem(first, new Product(2L, "B", "B", 1.0, ""), 1, 1.0),
                new OrderItem(first, new Product(3L, "C", "C", 1.0, ""), 2, 1.0),
                new OrderItem(second, new Product(4L, "D", "D", 1.0, ""), 1, 1.0)));
        assertEquals(2, product.getOrders().size());
        assertTrue(product.getOrders().contains(first));
        assertTrue(product.getOrders().contains(second));
    }

    @Test void entitiesWithSameIdAreEqualAndDifferentTypesAreNot() {
        assertEquals(new User(1L, "A", "a", "1", "p"), new User(1L, "B", "b", "2", "q"));
        assertEquals(new Category(1L, "A"), new Category(1L, "B"));
        assertEquals(new Product(1L, "A", "a", 1.0, ""), new Product(1L, "B", "b", 2.0, ""));
        assertNotEquals(new User(1L, "A", "a", "1", "p"), new Category(1L, "A"));
    }

    @Test void compositeKeyUsesBothAssociations() {
        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, null);
        Product product = new Product(1L, "A", "a", 1.0, "");
        OrderItemPk first = new OrderItemPk();
        first.setOrder(order);
        first.setProduct(product);
        OrderItemPk second = new OrderItemPk();
        second.setOrder(order);
        second.setProduct(product);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test void paymentAssociatesOrderAndMoment() {
        Order order = new Order();
        Instant moment = Instant.parse("2019-06-20T19:53:07Z");
        Payment payment = new Payment(1L, moment, order);
        assertEquals(1L, payment.getId());
        assertEquals(moment, payment.getMoment());
        assertSame(order, payment.getOrder());
        payment.setOrder(null);
        assertNull(payment.getOrder());
    }
}

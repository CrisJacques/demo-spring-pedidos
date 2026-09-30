package com.example.demospringpedidos.services;

import com.example.demospringpedidos.dto.OrderItemRequestDto;
import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderItemRepository;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
// A anotação @Component permite que a classe seja registrada no Component Registration do Spring, habilitando-a para ser injetada como dependência usando o @Autowired
// Existem outras anotações que são equivalentes, mas que tem semântica, como @Service para registrar Services e @Repository para registrar Repositories
// No caso do UserRepository, é opcional colocar a anotação @Repository, pois ele herda da interface JpaRepository que já está registrada como componente do Spring
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private Clock clock;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(Long id){
        Optional<Order> obj = repository.findById(id);
        return obj.get();
    }

    @Transactional
    public Order insert(OrderRequestDto request) {
        validateRequest(request);

        User client = userService.findById(request.clientId());
        Order order = new Order(null, clock.instant(), OrderStatus.WAITING_PAYMENT, client);

        List<OrderItem> items = createOrderItems(request, order);

        order.getItems().addAll(items);
        Order savedOrder = repository.save(order);
        orderItemRepository.saveAll(items);

        return savedOrder;
    }

    private void validateRequest(OrderRequestDto request) {
        if (request == null) {
            throw new BusinessException("Order body is required.");
        }
        if (request.clientId() == null) {
            throw new BusinessException("Client id is required.");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException("At least one item is required.");
        }
        for (OrderItemRequestDto requestedItem : request.items()) {
            validateOrderItem(requestedItem);
        }
    }

    private void validateOrderItem(OrderItemRequestDto requestedItem) {
        if (requestedItem == null) {
            throw new BusinessException("Order items cannot be null.");
        }
        if (requestedItem.productId() == null) {
            throw new BusinessException("Product id is required for each item.");
        }
        if (requestedItem.quantity() == null || requestedItem.quantity() <= 0) {
            throw new BusinessException("Item quantity must be greater than zero.");
        }
    }

    private List<OrderItem> createOrderItems(OrderRequestDto request, Order createdOrder) {
        Set<Long> productIds = new LinkedHashSet<>();
        for (OrderItemRequestDto requestedItem : request.items()) {
            productIds.add(requestedItem.productId());
        }

        Map<Long, Product> productsById = new LinkedHashMap<>();
        for (Product product : productService.findAllById(productIds)) {
            productsById.put(product.getId(), product);
        }

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDto requestedItem : request.items()) {
            Product product = productsById.get(requestedItem.productId());
            OrderItem item = new OrderItem(createdOrder, product, requestedItem.quantity(), product.getPrice());
            items.add(item);
        }

        return items;
    }

}

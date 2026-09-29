package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderItemRepository;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order findById(Long id){
        Optional<Order> obj = repository.findById(id);
        return obj.get();
    }

    @Transactional
    public Order insert(Order request) {
        validateRequest(request);

        User client = userService.findById(request.getClient().getId());
        Order order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, client);

        List<OrderItem> items = createOrderItems(request, order);

        order.getItems().addAll(items);
        Order savedOrder = repository.save(order);
        orderItemRepository.saveAll(items);

        return savedOrder;
    }

    private void validateRequest(Order request) {
        if (request == null) {
            throw new BusinessException("Order body is required.");
        }
        if (request.getClient() == null || request.getClient().getId() == null) {
            throw new BusinessException("Client id is required.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("At least one item is required.");
        }
        for (OrderItem requestedItem : request.getItems()) {
            validateOrderItem(requestedItem);
        }
    }

    private void validateOrderItem(OrderItem requestedItem) {
        if (requestedItem == null) {
            throw new BusinessException("Order items cannot be null.");
        }
        if (requestedItem.getProduct() == null || requestedItem.getProduct().getId() == null) {
            throw new BusinessException("Product id is required for each item.");
        }
        if (requestedItem.getQuantity() == null || requestedItem.getQuantity() <= 0) {
            throw new BusinessException("Item quantity must be greater than zero.");
        }
    }

    private List<OrderItem> createOrderItems(Order request, Order createdOrder) {
        List<OrderItem> items = new ArrayList<>();

        for (OrderItem requestedItem : request.getItems()) {
            Product product = productService.findById(requestedItem.getProduct().getId());
            OrderItem item = new OrderItem(createdOrder, product, requestedItem.getQuantity(), product.getPrice());
            items.add(item);
        }

        return items;
    }

}

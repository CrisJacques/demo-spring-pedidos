package com.example.demospringpedidos.services;

import com.example.demospringpedidos.dto.OrderItemRequestDto;
import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.OrderItem;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
// A anotação @Component permite que a classe seja registrada no Component Registration do Spring, habilitando-a para ser injetada como dependência usando o @Autowired
// Existem outras anotações que são equivalentes, mas que tem semântica, como @Service para registrar Services e @Repository para registrar Repositories
// No caso do UserRepository, é opcional colocar a anotação @Repository, pois ele herda da interface JpaRepository que já está registrada como componente do Spring
public class OrderService {

    @Autowired
    private OrderRepository repository;

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
        Map<Long, Integer> quantitiesByProduct = aggregateQuantities(request);

        User client = userService.findById(request.clientId());
        Order order = new Order(null, clock.instant(), OrderStatus.WAITING_PAYMENT, client);

        List<OrderItem> items = createOrderItems(quantitiesByProduct, order);

        order.getItems().addAll(items);

        return repository.save(order);
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

    private Map<Long, Integer> aggregateQuantities(OrderRequestDto request) {
        Map<Long, Integer> quantitiesByProduct = new LinkedHashMap<>();
        for (OrderItemRequestDto requestedItem : request.items()) {
            Integer currentQuantity = quantitiesByProduct.get(requestedItem.productId());
            if (currentQuantity == null) {
                quantitiesByProduct.put(requestedItem.productId(), requestedItem.quantity());
            } else {
                if (requestedItem.quantity() > Integer.MAX_VALUE - currentQuantity) {
                    throw new BusinessException("Total quantity for a product exceeds the supported limit.");
                }
                quantitiesByProduct.put(requestedItem.productId(), currentQuantity + requestedItem.quantity());
            }
        }

        return quantitiesByProduct;
    }

    private List<OrderItem> createOrderItems(Map<Long, Integer> quantitiesByProduct, Order createdOrder) {
        Map<Long, Product> productsById = new LinkedHashMap<>();
        for (Product product : productService.findAllById(quantitiesByProduct.keySet())) {
            productsById.put(product.getId(), product);
        }

        List<OrderItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> requestedProduct : quantitiesByProduct.entrySet()) {
            Product product = productsById.get(requestedProduct.getKey());
            OrderItem item = new OrderItem(createdOrder, product, requestedProduct.getValue(), product.getPrice());
            items.add(item);
        }

        return items;
    }

}

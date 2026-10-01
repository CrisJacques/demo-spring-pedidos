package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderResourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void postAggregatesRepeatedProductItemsAndExistingGetRoutesStillWork() throws Exception {
        User client = userRepository.findAll().stream()
                .filter(user -> "Maria Brown".equals(user.getName()))
                .findFirst()
                .orElseThrow();
        Product product = productRepository.findAll().stream()
                .filter(item -> "The Lord of the Rings".equals(item.getName()))
                .findFirst()
                .orElseThrow();

        var response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": %d,
                                  "items": [
                                    {"productId": %d, "quantity": 1},
                                    {"productId": %d, "quantity": 1}
                                  ]
                                }
                                """.formatted(client.getId(), product.getId(), product.getId())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/orders/4")))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"))
                .andExpect(jsonPath("$.client.id").value(client.getId()))
                .andExpect(jsonPath("$.payment").doesNotExist())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(product.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(90.5))
                .andExpect(jsonPath("$.total").value(181.0))
                .andExpect(jsonPath("$.moment").isNotEmpty())
                .andReturn();

        String location = response.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"))
                .andExpect(jsonPath("$.client.id").value(client.getId()));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 4)]").exists());
    }

    @Test
    void putPaidOrderTwicePreservesExistingPayment() throws Exception {
        Product book = productRepository.findAll().stream()
                .filter(item -> "The Lord of the Rings".equals(item.getName()))
                .findFirst()
                .orElseThrow();
        Product laptop = productRepository.findAll().stream()
                .filter(item -> "Macbook Pro".equals(item.getName()))
                .findFirst()
                .orElseThrow();
        String request = """
                {
                  "orderStatus": 2,
                  "items": [
                    {"productId": %d, "quantity": 3},
                    {"productId": %d, "quantity": 4}
                  ]
                }
                """.formatted(book.getId(), laptop.getId());

        mockMvc.perform(put("/orders/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.payment.id").value(2));

        var paymentAfterFirstUpdate = orderRepository.findById(2L).orElseThrow().getPayment();
        var paymentMoment = paymentAfterFirstUpdate.getMoment();

        mockMvc.perform(put("/orders/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.payment.id").value(paymentAfterFirstUpdate.getId()));

        var paymentAfterSecondUpdate = orderRepository.findById(2L).orElseThrow().getPayment();
        assertEquals(paymentMoment, paymentAfterSecondUpdate.getMoment());
    }

    @Test
    void putReplacesPersistedOrderItems() throws Exception {
        Product replacement = productRepository.findAll().stream()
                .filter(item -> "The Lord of the Rings".equals(item.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/orders/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderStatus": 1,
                                  "items": [
                                    {"productId": %d, "quantity": 3}
                                  ]
                                }
                                """.formatted(replacement.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(replacement.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(3));

        entityManager.flush();
        entityManager.clear();

        var persistedOrder = orderRepository.findById(3L).orElseThrow();
        assertEquals(1, persistedOrder.getItems().size());
        assertEquals(replacement.getId(), persistedOrder.getItems().get(0).getProduct().getId());
        assertEquals(3, persistedOrder.getItems().get(0).getQuantity());

        mockMvc.perform(get("/orders/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product.id").value(replacement.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }
}

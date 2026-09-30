package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private ProductRepository productRepository;

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
}

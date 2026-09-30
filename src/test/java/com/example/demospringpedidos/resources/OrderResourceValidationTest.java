package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.dto.OrderRequestDto;
import com.example.demospringpedidos.resources.exceptions.ResourceExceptionHandler;
import com.example.demospringpedidos.services.OrderService;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.endsWith;

@ExtendWith(MockitoExtension.class)
class OrderResourceValidationTest {
    @Mock
    private OrderService service;

    @InjectMocks
    private OrderResource resource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resource)
                .setControllerAdvice(new ResourceExceptionHandler())
                .build();
    }

    @Test
    void postRejectsInvalidRequestFields() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"productId":1,"quantity":2}]}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clientId":1,"items":[{"quantity":2}]}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clientId":1,"items":[{"productId":1,"quantity":0}]}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clientId":1,"items":[]}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postCreatesOrderFromTheRequestDto() throws Exception {
        Order createdOrder = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, null);
        createdOrder.setId(42L);
        when(service.insert(any())).thenReturn(createdOrder);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 1,
                                  "items": [
                                    {"productId": 3, "quantity": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/orders/42")));

        ArgumentCaptor<OrderRequestDto> requestCaptor = ArgumentCaptor.forClass(OrderRequestDto.class);
        verify(service).insert(requestCaptor.capture());
        assertEquals(1L, requestCaptor.getValue().clientId());
        assertEquals(3L, requestCaptor.getValue().items().get(0).productId());
        assertEquals(2, requestCaptor.getValue().items().get(0).quantity());
    }

    @Test
    void deleteReturnsNoContentAndDelegatesToService() throws Exception {
        mockMvc.perform(delete("/orders/42"))
                .andExpect(status().isNoContent());

        verify(service).delete(42L);
    }

    @Test
    void deleteReturnsNotFoundWhenOrderDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException(42L)).when(service).delete(42L);

        mockMvc.perform(delete("/orders/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/orders/42"));

        verify(service).delete(42L);
    }
}

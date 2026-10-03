package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserResourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = userRepository.findAll().stream()
                .filter(user -> "Maria Brown".equals(user.getName()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void getAllUsersExcludesPasswordOnReturn() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].password").doesNotExist());
    }

    @Test
    void getUserByIdExcludesPasswordOnReturn() throws Exception {
        mockMvc.perform(get("/users/{id}", existingUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void postUsersAcceptsPasswordAndExcludesItOnReturn() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New User",
                                  "email": "new.user@example.com",
                                  "phone": "999999999",
                                  "password": "Abcdefg1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/users/\\d+")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void putUserAcceptsPasswordAndExcludesItOnReturn() throws Exception {
        mockMvc.perform(put("/users/{id}", existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Maria Updated",
                                  "email": "maria.updated@example.com",
                                  "phone": "988888888",
                                  "password": "Abcdefg2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Updated"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}

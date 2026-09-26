package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.resources.exceptions.ResourceExceptionHandler;
import com.example.demospringpedidos.services.UserService;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserResourceValidationTest {
    @Mock
    private UserService service;

    @InjectMocks
    private UserResource resource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resource)
                .setControllerAdvice(new ResourceExceptionHandler())
                .build();
    }

    @Test
    void postRejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsBlankRequiredFields() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","email":"user@example.com","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":" ","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":" ","password":"123456"}
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":" "}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postAcceptsAllRequiredFields() throws Exception {
        when(service.insert(any(User.class))).thenReturn(
                new User(42L, "User", "user@example.com", "999999999", "123456"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void postRejectsExistingEmail() throws Exception {
        when(service.insert(any(User.class))).thenThrow(new BusinessException("Email already exists."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"123456"}
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }
}

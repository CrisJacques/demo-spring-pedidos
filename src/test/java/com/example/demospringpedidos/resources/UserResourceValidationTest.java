package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.dto.UserMapper;
import com.example.demospringpedidos.dto.UserRequestDto;
import com.example.demospringpedidos.dto.UserResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserResourceValidationTest {
    @Mock
    private UserService service;

    @Mock
    private UserMapper mapper;

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
    void postRejectsInvalidEmailPhoneAndPassword() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"invalid-email","phone":"999999999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: Not valid email"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999-999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("phone: Phone must contain only numbers"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"password"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "password: The password must be at least 8 characters long, including a number, "
                                + "an uppercase letter, and a lowercase letter."));

        verifyNoInteractions(service);
    }

    @Test
    void putRejectsInvalidEmailPhoneAndPassword() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"invalid-email","phone":"999999999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: Not valid email"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999-999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("phone: Phone must contain only numbers"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"password"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "password: The password must be at least 8 characters long, including a number, "
                                + "an uppercase letter, and a lowercase letter."));

        verifyNoInteractions(service);
    }

    @Test
    void postAcceptsAllRequiredFields() throws Exception {
        User entity = new User(42L, "User", "user@example.com", "999999999", "Abcdefg1");
        when(mapper.toEntity(any(UserRequestDto.class))).thenReturn(entity);
        when(service.insert(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                new UserResponseDto(42L, "User", "user@example.com", "999999999"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void postRejectsExistingEmail() throws Exception {
        User entity = new User(null, "User", "user@example.com", "999999999", "Abcdefg1");
        when(mapper.toEntity(any(UserRequestDto.class))).thenReturn(entity);
        when(service.insert(entity)).thenThrow(new BusinessException("Email already exists."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"user@example.com","phone":"999999999","password":"Abcdefg1"}
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }
}

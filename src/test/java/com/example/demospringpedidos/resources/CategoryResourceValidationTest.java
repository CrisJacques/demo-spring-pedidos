package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.resources.exceptions.ResourceExceptionHandler;
import com.example.demospringpedidos.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryResourceValidationTest {
    private static final String VALIDATION_ERROR = "Validation error";
    private static final String REQUIRED_FIELDS_MISSING = "Required fields are missing";

    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryResource resource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resource)
                .setControllerAdvice(new ResourceExceptionHandler())
                .build();
    }

    @Test
    void postRejectsMissingName() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value(REQUIRED_FIELDS_MISSING));

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsBlankName() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value(REQUIRED_FIELDS_MISSING));

        verifyNoInteractions(service);
    }

    @Test
    void putRejectsMissingName() throws Exception {
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value(REQUIRED_FIELDS_MISSING));

        verifyNoInteractions(service);
    }

    @Test
    void putRejectsBlankName() throws Exception {
        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value(REQUIRED_FIELDS_MISSING));

        verifyNoInteractions(service);
    }
}

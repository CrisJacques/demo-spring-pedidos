package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.services.ProductService;
import com.example.demospringpedidos.resources.exceptions.ResourceExceptionHandler;
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

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ProductResourceValidationTest {
    @Mock
    private ProductService service;

    @InjectMocks
    private ProductResource resource;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resource)
                .setControllerAdvice(new ResourceExceptionHandler())
                .build();
    }

    @Test
    void postRejectsMissingName() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Description","price":10.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsMissingDescription() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Product","price":10.0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsMissingPrice() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Product","description":"Description"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsBlankName() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","description":"Description","price":10.0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsBlankDescription() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Product","description":"  ","price":10.0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsZeroPrice() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Product","description":"Description","price":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("price: Price must be greater than zero"));

        verifyNoInteractions(service);
    }

    @Test
    void postRejectsNegativePrice() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Product","description":"Description","price":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("price: Price must be greater than zero"));

        verifyNoInteractions(service);
    }

    @Test
    void postCreatesProductAndReturnsLocation() throws Exception {
        Product persistedProduct = new Product(42L, "Notebook", "Gaming notebook", 2500.0, "");
        when(service.insert(any(Product.class))).thenReturn(persistedProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Notebook","description":"Gaming notebook","price":2500.0,"imgUrl":""}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/products/42")))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.description").value("Gaming notebook"))
                .andExpect(jsonPath("$.price").value(2500.0));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).insert(productCaptor.capture());
        Product submittedProduct = productCaptor.getValue();
        assertNull(submittedProduct.getId());
        assertEquals("Notebook", submittedProduct.getName());
        assertEquals("Gaming notebook", submittedProduct.getDescription());
        assertEquals(2500.0, submittedProduct.getPrice());
        assertEquals("", submittedProduct.getImgUrl());
    }

    @Test
    void putUpdatesProductAndReturnsUpdatedProduct() throws Exception {
        Product updatedProduct = new Product(42L, "Notebook Pro", "Updated description", 2800.0, "notebook.jpg");
        updatedProduct.getCategories().add(new Category(3L, "Electronics"));
        when(service.update(eq(42L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Notebook Pro",
                                  "description":"Updated description",
                                  "price":2800.0,
                                  "imgUrl":"notebook.jpg",
                                  "categories":[{"id":3}]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Notebook Pro"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(2800.0))
                .andExpect(jsonPath("$.imgUrl").value("notebook.jpg"))
                .andExpect(jsonPath("$.categories[0].id").value(3));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).update(eq(42L), productCaptor.capture());
        Product submittedProduct = productCaptor.getValue();
        assertNull(submittedProduct.getId());
        assertEquals("Notebook Pro", submittedProduct.getName());
        assertEquals("Updated description", submittedProduct.getDescription());
        assertEquals(2800.0, submittedProduct.getPrice());
        assertEquals("notebook.jpg", submittedProduct.getImgUrl());
        assertEquals(3L, submittedProduct.getCategories().iterator().next().getId());
    }

    @Test
    void putRejectsInvalidPriceAndDoesNotCallService() throws Exception {
        mockMvc.perform(put("/products/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Notebook","description":"Description","price":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("price: Price must be greater than zero"));

        verifyNoInteractions(service);
    }

    @Test
    void putRejectsMissingNameAndDoesNotCallService() throws Exception {
        mockMvc.perform(put("/products/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Description","price":10.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("name: Field is required"));

        verifyNoInteractions(service);
    }

    @Test
    void putRejectsMissingDescriptionAndDoesNotCallService() throws Exception {
        mockMvc.perform(put("/products/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Notebook","price":100.0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("description: Field is required"));

        verifyNoInteractions(service);
    }
}

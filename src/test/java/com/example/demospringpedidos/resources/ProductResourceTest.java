package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.services.ProductService;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {
    @Mock
    private ProductService service;

    @InjectMocks
    private ProductResource resource;

    @Test
    void deleteReturnsNoContentAndDelegatesToService() {
        var response = resource.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).delete(1L);
    }

    @Test
    void deletePropagatesNotFoundFromService() {
        doThrow(new ResourceNotFoundException(9L)).when(service).delete(9L);

        assertThrows(ResourceNotFoundException.class, () -> resource.delete(9L));

        verify(service).delete(9L);
    }

    @Test
    void deletePropagatesDatabaseErrorFromService() {
        doThrow(new DatabaseException("Product has associated orders.")).when(service).delete(1L);

        assertThrows(DatabaseException.class, () -> resource.delete(1L));

        verify(service).delete(1L);
    }
}

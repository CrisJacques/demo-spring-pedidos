package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.services.ProductService;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {
    @Mock
    private ProductService service;

    @InjectMocks
    private ProductResource resource;

    @InjectMocks
    private ProductResource productResource;

    @Test void productResourceReturnsListAndItem() {
        List<Product> list = List.of(new Product(1L, "Book", "Description", 10.0, ""));
        when(service.findAll()).thenReturn(list);
        when(service.findById(1L)).thenReturn(list.get(0));
        assertSame(list, productResource.findAll().getBody());
        assertSame(list.get(0), productResource.findById(1L).getBody());
    }

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

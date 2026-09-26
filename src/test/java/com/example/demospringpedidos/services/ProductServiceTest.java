package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository repository;
    @InjectMocks ProductService service;

    @Test void findAllReturnsRepositoryData() {
        List<Product> products = List.of(new Product(1L, "Book", "Description", 10.0, ""));
        when(repository.findAll()).thenReturn(products);
        assertSame(products, service.findAll());
    }

    @Test void findByIdReturnsProductWhenFound() {
        Product product = new Product(1L, "Book", "Description", 10.0, "");
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        assertSame(product, service.findById(1L));
    }

    @Test void findByIdThrowsWhenProductIsMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById(1L));
    }

    @Test void insertSavesProductWhenNameIsUnique() {
        Product product = new Product(null, "Book", "Description", 10.0, "");
        when(repository.existsByNameIgnoreCase("Book")).thenReturn(false);
        when(repository.save(product)).thenReturn(product);

        assertSame(product, service.insert(product));

        verify(repository).existsByNameIgnoreCase("Book");
        verify(repository).save(product);
    }

    @Test void insertThrowsBusinessExceptionWhenNameAlreadyExists() {
        Product product = new Product(null, "Book", "Description", 10.0, "");
        when(repository.existsByNameIgnoreCase("Book")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.insert(product)
        );

        assertEquals("Product already exists.", exception.getMessage());
        verify(repository).existsByNameIgnoreCase("Book");
        verify(repository, never()).save(any(Product.class));
    }
}

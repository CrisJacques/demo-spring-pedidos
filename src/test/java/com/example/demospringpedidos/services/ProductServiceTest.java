package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.repositories.ProductRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
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

    @Test void updateChangesProductDataAndCategories() {
        Product actualProduct = new Product(1L, "Old name", "Old description", 10.0, "old.jpg");
        actualProduct.getCategories().add(new Category(1L, "Old category"));
        Product newProductInfo = new Product(null, "New name", "New description", 25.0, "new.jpg");
        newProductInfo.getCategories().add(new Category(2L, "New category"));

        when(repository.existsByNameIgnoreCase("New name")).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(actualProduct));
        when(repository.save(actualProduct)).thenReturn(actualProduct);

        Product result = service.update(1L, newProductInfo);

        assertSame(actualProduct, result);
        assertEquals("New name", actualProduct.getName());
        assertEquals("New description", actualProduct.getDescription());
        assertEquals(25.0, actualProduct.getPrice());
        assertEquals("new.jpg", actualProduct.getImgUrl());
        assertEquals(newProductInfo.getCategories(), actualProduct.getCategories());
        verify(repository).existsByNameIgnoreCase("New name");
        verify(repository).findById(1L);
        verify(repository).save(actualProduct);
    }

    @Test void updateThrowsBusinessExceptionWhenNameAlreadyExists() {
        Product newProductInfo = new Product(null, "Existing name", "Description", 25.0, "");
        when(repository.existsByNameIgnoreCase("Existing name")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.update(1L, newProductInfo)
        );

        assertEquals("New product name already exists.", exception.getMessage());
        verify(repository).existsByNameIgnoreCase("Existing name");
        verify(repository, never()).findById(anyLong());
        verify(repository, never()).save(any(Product.class));
    }

    @Test void updateThrowsResourceNotFoundWhenProductDoesNotExist() {
        Product newProductInfo = new Product(null, "New name", "Description", 25.0, "");
        when(repository.existsByNameIgnoreCase("New name")).thenReturn(false);
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, newProductInfo));

        verify(repository).findById(9L);
        verify(repository, never()).save(any(Product.class));
    }

    @Test void deleteRemovesExistingProduct() {
        Product product = new Product(1L, "Book", "Description", 10.0, "");
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test void deleteThrowsWhenProductDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));

        verify(repository).findById(9L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test void deleteConvertsIntegrityViolationToDatabaseException() {
        Product product = new Product(1L, "Book", "Description", 10.0, "");
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        doThrow(new DataIntegrityViolationException("constraint")).when(repository).deleteById(1L);

        DatabaseException exception = assertThrows(DatabaseException.class, () -> service.delete(1L));

        assertEquals("Product has associated orders.", exception.getMessage());
        verify(repository).deleteById(1L);
    }
}

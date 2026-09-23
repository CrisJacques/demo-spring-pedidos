package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.repositories.CategoryRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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
class CategoryServiceTest {
    @Mock CategoryRepository repository;
    @InjectMocks CategoryService service;

    @Test void findAllReturnsRepositoryData() {
        List<Category> categories = List.of(new Category(1L, "Books"));
        when(repository.findAll()).thenReturn(categories);
        assertSame(categories, service.findAll());
    }

    @Test void findByIdReturnsCategoryWhenFound() {
        Category category = new Category(1L, "Books");
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        assertSame(category, service.findById(1L));
    }

    @Test void findByIdThrowsWhenCategoryIsMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById(1L));
    }

    @Test void insertSavesCategoryWhenNameIsUnique() {
        Category category = new Category(null, "Electronics");

        when(repository.findAll()).thenReturn(List.of(new Category(1L, "Books")));
        when(repository.save(category)).thenReturn(category);

        assertSame(category, service.insert(category));
        verify(repository).save(category);
    }

    @Test void insertThrowsBusinessExceptionWhenCategoryAlreadyExists() {
        Category category = new Category(null, "Books");

        when(repository.findAll()).thenReturn(List.of(new Category(1L, "Books")));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.insert(category)
        );

        assertEquals("Category already exists", exception.getMessage());
        verify(repository, never()).save(any(Category.class));
    }

    @Test void updateChangesCategoryNameWhenNewNameIsUnique() {
        Category entity = new Category(1L, "Books");
        Category input = new Category(null, "Electronics");

        when(repository.getReferenceById(1L)).thenReturn(entity);
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        Category result = service.update(1L, input);

        assertSame(entity, result);
        assertEquals("Electronics", entity.getName());
        verify(repository).save(entity);
    }

    @Test void updateThrowsResourceNotFoundWhenCategoryDoesNotExist() {
        when(repository.getReferenceById(9L)).thenThrow(new EntityNotFoundException());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(9L, new Category(null, "Electronics"))
        );

        assertEquals("Resource not found. Id 9", exception.getMessage());
        verify(repository, never()).save(any(Category.class));
    }

    @Test void updateThrowsBusinessExceptionWhenNewNameAlreadyExists() {
        Category entity = new Category(1L, "Books");
        Category input = new Category(null, "Electronics");

        when(repository.getReferenceById(1L)).thenReturn(entity);
        when(repository.findAll()).thenReturn(List.of(
                entity,
                new Category(2L, "Electronics")
        ));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.update(1L, input)
        );

        assertEquals("New name for category already exists", exception.getMessage());
        verify(repository, never()).save(any(Category.class));
    }
}

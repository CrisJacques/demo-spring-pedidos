package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.repositories.CategoryRepository;
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
}

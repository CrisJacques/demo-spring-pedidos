package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.repositories.CategoryRepository;
import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock private CategoryRepository repository;
    @InjectMocks private CategoryService service;

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
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test void insertSavesCategoryWhenNameIsUnique() {
        Category category = new Category(null, "Electronics");

        when(repository.save(category)).thenReturn(category);

        assertSame(category, service.insert(category));
        verify(repository).save(category);
    }

    @Test void insertThrowsBusinessExceptionWhenCategoryAlreadyExists() {
        Category category = new Category(null, "Books");

        when(repository.existsByNameIgnoreCase("Books")).thenReturn(true);

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
        when(repository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.update(1L, input)
        );

        assertEquals("New name for category already exists", exception.getMessage());
        verify(repository, never()).save(any(Category.class));
    }

    @Test void deleteRemovesExistingCategory() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Books")));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test void deleteDoesNotDeleteMissingCategory() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));

        verify(repository, never()).deleteById(anyLong());
    }

    @Test void deleteConvertsIntegrityViolationToDatabaseException() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Books")));
        doThrow(new DataIntegrityViolationException("constraint")).when(repository).deleteById(1L);

        DatabaseException exception = assertThrows(DatabaseException.class, () -> service.delete(1L));

        assertEquals("Category has associated products", exception.getMessage());
        verify(repository).deleteById(1L);
    }
}

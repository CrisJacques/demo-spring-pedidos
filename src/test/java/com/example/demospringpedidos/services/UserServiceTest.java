package com.example.demospringpedidos.services;

import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.repositories.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository repository;
    @InjectMocks UserService service;

    @Test void findAllReturnsRepositoryData() {
        List<User> users = List.of(new User(1L, "Maria", "maria@test.com", "999", "secret"));
        when(repository.findAll()).thenReturn(users);
        assertSame(users, service.findAll());
    }

    @Test void findByIdReturnsUserWhenFound() {
        User user = new User(1L, "Maria", "maria@test.com", "999", "secret");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        assertSame(user, service.findById(1L));
    }

    @Test void findByIdThrowsDomainExceptionWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.findById(7L));
        assertEquals("Resource not found. Id 7", exception.getMessage());
    }

    @Test void insertDelegatesToRepository() {
        User user = new User(null, "Maria", "maria@test.com", "999", "secret");
        when(repository.save(user)).thenReturn(user);
        assertSame(user, service.insert(user));
        verify(repository).save(user);
    }

    @Test void deleteRemovesExistingUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(new User()));
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test void deletePropagatesNotFoundBeforeDelete() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test void deleteConvertsIntegrityViolationToDatabaseException() {
        when(repository.findById(1L)).thenReturn(Optional.of(new User()));
        doThrow(new DataIntegrityViolationException("constraint")).when(repository).deleteById(1L);
        DatabaseException exception = assertThrows(DatabaseException.class, () -> service.delete(1L));
        assertEquals("constraint", exception.getMessage());
    }

    @Test void updateChangesEditableFieldsAndPreservesPassword() {
        User entity = new User(1L, "Old", "old@test.com", "111", "keep");
        User input = new User(null, "New", "new@test.com", "222", "replace-attempt");
        when(repository.getReferenceById(1L)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        User result = service.update(1L, input);
        assertSame(entity, result);
        assertEquals("New", entity.getName());
        assertEquals("new@test.com", entity.getEmail());
        assertEquals("222", entity.getPhone());
        assertEquals("keep", entity.getPassword());
    }

    @Test void updateConvertsMissingEntityToResourceNotFound() {
        when(repository.getReferenceById(9L)).thenThrow(new EntityNotFoundException());
        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, new User()));
    }
}

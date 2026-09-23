package com.example.demospringpedidos.services.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BusinessExceptionTest {

    @Test
    void constructorSetsMessage() {
        BusinessException exception = new BusinessException("Category already exists");

        assertEquals("Category already exists", exception.getMessage());
    }

    @Test
    void extendsRuntimeException() {
        BusinessException exception = new BusinessException("Business error");

        assertInstanceOf(RuntimeException.class, exception);
    }
}

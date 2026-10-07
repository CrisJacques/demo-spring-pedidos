package com.example.demospringpedidos.resources.exceptions;

import com.example.demospringpedidos.services.exceptions.BusinessException;
import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceExceptionHandlerTest {
    private final ResourceExceptionHandler handler = new ResourceExceptionHandler();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test void resourceNotFoundBuildsNotFoundResponse() {
        when(request.getRequestURI()).thenReturn("/users/7");
        var response = handler.resourceNotFound(new ResourceNotFoundException(7L), request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getError());
        assertEquals("Resource not found. Id 7", response.getBody().getMessage());
        assertEquals("/users/7", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test void databaseErrorBuildsBadRequestResponse() {
        when(request.getRequestURI()).thenReturn("/users/1");
        var response = handler.databaseError(new DatabaseException("constraint"), request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Database error", response.getBody().getError());
        assertEquals("constraint", response.getBody().getMessage());
        assertEquals("/users/1", response.getBody().getPath());
    }

    @Test void validationErrorCombinesFieldMessages() {
        when(request.getRequestURI()).thenReturn("/users");
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("user", "name", "Field is required"),
                new FieldError("user", "email", "Not valid email")));

        var response = handler.validationError(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody().getError());
        assertEquals("name: Field is required; email: Not valid email", response.getBody().getMessage());
        assertEquals("/users", response.getBody().getPath());
    }

    @Test void businessErrorBuildsUnprocessableResponse() {
        when(request.getRequestURI()).thenReturn("/categories");

        var response = handler.businessError(new BusinessException("Category already exists"), request);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("Business error", response.getBody().getError());
        assertEquals("Category already exists", response.getBody().getMessage());
        assertEquals("/categories", response.getBody().getPath());
    }

    @Test void stateErrorBuildsUnprocessableResponse() {
        when(request.getRequestURI()).thenReturn("/orders/1");

        var response = handler.stateError(new IllegalStateException("Invalid state"), request);

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertEquals("Object state error", response.getBody().getError());
        assertEquals("Invalid state", response.getBody().getMessage());
        assertEquals("/orders/1", response.getBody().getPath());
    }
}

package com.example.demospringpedidos.resources.exceptions;

import com.example.demospringpedidos.services.exceptions.DatabaseException;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}

package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.services.CategoryService;
import com.example.demospringpedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryResourceTest {
    @Mock private CategoryService categoryService;
    @InjectMocks private CategoryResource categoryResource;

    @AfterEach void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test void categoryResourceReturnsListAndItem() {
        List<Category> list = List.of(new Category(1L, "Books"));
        when(categoryService.findAll()).thenReturn(list);
        Category category = list.get(0);
        when(categoryService.findById(1L)).thenReturn(category);
        assertEquals(HttpStatus.OK, categoryResource.findAll().getStatusCode());
        assertSame(list, categoryResource.findAll().getBody());
        assertSame(category, categoryResource.findById(1L).getBody());
    }

    @Test void categoryResourcePropagatesServiceError() {
        when(categoryService.findById(1L)).thenThrow(new RuntimeException("failure"));
        assertThrows(RuntimeException.class, () -> categoryResource.findById(1L));
    }

    @Test void categoryResourceInsertReturnsCreatedResourceAndLocation() {
        Category category = new Category(1L, "Electronics");
        when(categoryService.insert(category)).thenReturn(category);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/categories");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = categoryResource.insert(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/categories/1", response.getHeaders().getLocation().getPath());
        assertSame(category, response.getBody());
        verify(categoryService).insert(category);
    }

    @Test void categoryResourceUpdateReturnsUpdatedCategory() {
        Category input = new Category(null, "Smartphones");
        Category updated = new Category(1L, "Smartphones");
        when(categoryService.update(1L, input)).thenReturn(updated);

        var response = categoryResource.update(1L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(categoryService).update(1L, input);
    }

    @Test void categoryResourceUpdatePropagatesNotFound() {
        Category input = new Category(null, "Smartphones");
        when(categoryService.update(9L, input)).thenThrow(new ResourceNotFoundException(9L));

        assertThrows(ResourceNotFoundException.class, () -> categoryResource.update(9L, input));
        verify(categoryService).update(9L, input);
    }

}

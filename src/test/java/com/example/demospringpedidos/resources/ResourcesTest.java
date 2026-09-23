package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.entities.Category;
import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.Product;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.services.CategoryService;
import com.example.demospringpedidos.services.OrderService;
import com.example.demospringpedidos.services.ProductService;
import com.example.demospringpedidos.services.UserService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourcesTest {
    @Mock CategoryService categoryService;
    @Mock ProductService productService;
    @Mock OrderService orderService;
    @Mock UserService userService;
    @InjectMocks CategoryResource categoryResource;
    @InjectMocks ProductResource productResource;
    @InjectMocks OrderResource orderResource;
    @InjectMocks UserResource userResource;

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

    @Test void productResourceReturnsListAndItem() {
        List<Product> list = List.of(new Product(1L, "Book", "Description", 10.0, ""));
        when(productService.findAll()).thenReturn(list);
        when(productService.findById(1L)).thenReturn(list.get(0));
        assertSame(list, productResource.findAll().getBody());
        assertSame(list.get(0), productResource.findById(1L).getBody());
    }

    @Test void orderResourceReturnsListAndItem() {
        List<Order> list = List.of(new Order());
        when(orderService.findAll()).thenReturn(list);
        when(orderService.findById(1L)).thenReturn(list.get(0));
        assertSame(list, orderResource.findAll().getBody());
        assertSame(list.get(0), orderResource.findById(1L).getBody());
    }

    @Test void userResourceReturnsListAndItem() {
        List<User> list = List.of(new User(1L, "Maria", "maria@test.com", "999", "secret"));
        when(userService.findAll()).thenReturn(list);
        when(userService.findById(1L)).thenReturn(list.get(0));
        assertSame(list, userResource.findAll().getBody());
        assertSame(list.get(0), userResource.findById(1L).getBody());
    }

    @Test void userResourceInsertReturnsCreatedResourceAndLocation() {
        User user = new User(1L, "Maria", "maria@test.com", "999", "secret");
        when(userService.insert(user)).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertEquals(HttpStatus.CREATED, userResource.insert(user).getStatusCode());
        assertEquals("/users/1", userResource.insert(user).getHeaders().getLocation().getPath());
        assertSame(user, userResource.insert(user).getBody());
    }

    @Test void userResourceDeleteReturnsNoContent() {
        assertEquals(HttpStatus.NO_CONTENT, userResource.delete(1L).getStatusCode());
        verify(userService).delete(1L);
    }

    @Test void userResourceDeletePropagatesNotFound() {
        doThrow(new ResourceNotFoundException(1L)).when(userService).delete(1L);
        assertThrows(ResourceNotFoundException.class, () -> userResource.delete(1L));
    }

    @Test void userResourceUpdateReturnsUpdatedUser() {
        User input = new User(null, "New", "new@test.com", "222", "secret");
        User updated = new User(1L, "New", "new@test.com", "222", "secret");
        when(userService.update(1L, input)).thenReturn(updated);
        assertSame(updated, userResource.update(1L, input).getBody());
        verify(userService).update(1L, input);
    }

    @Test void userResourceUpdatePropagatesNotFound() {
        User input = new User();
        when(userService.update(1L, input)).thenThrow(new ResourceNotFoundException(1L));
        assertThrows(ResourceNotFoundException.class, () -> userResource.update(1L, input));
    }
}

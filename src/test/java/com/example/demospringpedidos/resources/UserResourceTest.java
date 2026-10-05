package com.example.demospringpedidos.resources;

import com.example.demospringpedidos.dto.UserMapper;
import com.example.demospringpedidos.dto.UserRequestDto;
import com.example.demospringpedidos.dto.UserResponseDto;
import com.example.demospringpedidos.entities.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {
    @Mock private UserService userService;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserResource userResource;

    @AfterEach void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test void userResourceReturnsListAndItem() {
        List<User> list = List.of(new User(1L, "Maria", "maria@test.com", "999", "secret"));
        UserResponseDto response = new UserResponseDto(1L, "Maria", "maria@test.com", "999");
        when(userService.findAll()).thenReturn(list);
        when(userService.findById(1L)).thenReturn(list.get(0));
        when(userMapper.toResponse(list.get(0))).thenReturn(response);
        assertEquals(List.of(response), userResource.findAll().getBody());
        assertSame(response, userResource.findById(1L).getBody());
    }

    @Test void userResourceInsertReturnsCreatedResourceAndLocation() {
        UserRequestDto requestDto = new UserRequestDto("Maria", "maria@test.com", "999", "Abcdefg1");
        User mappedUser = new User(null, "Maria", "maria@test.com", "999", "Abcdefg1");
        User user = new User(1L, "Maria", "maria@test.com", "999", "secret");
        UserResponseDto responseDto = new UserResponseDto(1L, "Maria", "maria@test.com", "999");
        when(userMapper.toEntity(requestDto)).thenReturn(mappedUser);
        when(userService.insert(mappedUser)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var response = userResource.insert(requestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/users/1", response.getHeaders().getLocation().getPath());
        assertSame(responseDto, response.getBody());
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
        UserRequestDto request = new UserRequestDto("New", "new@test.com", "222", "Abcdefg1");
        User input = new User(null, "New", "new@test.com", "222", "Abcdefg1");
        User updated = new User(1L, "New", "new@test.com", "222", "secret");
        UserResponseDto response = new UserResponseDto(1L, "New", "new@test.com", "222");
        when(userMapper.toEntity(request)).thenReturn(input);
        when(userService.update(1L, input)).thenReturn(updated);
        when(userMapper.toResponse(updated)).thenReturn(response);
        assertSame(response, userResource.update(1L, request).getBody());
        verify(userService).update(1L, input);
    }

    @Test void userResourceUpdatePropagatesNotFound() {
        UserRequestDto request = new UserRequestDto("New", "new@test.com", "222", "Abcdefg1");
        User input = new User(null, "New", "new@test.com", "222", "Abcdefg1");
        when(userMapper.toEntity(request)).thenReturn(input);
        when(userService.update(1L, input)).thenThrow(new ResourceNotFoundException(1L));
        assertThrows(ResourceNotFoundException.class, () -> userResource.update(1L, request));
    }
}

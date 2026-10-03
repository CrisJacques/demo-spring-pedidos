package com.example.demospringpedidos.dto;

import com.example.demospringpedidos.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto request) {
        return new User(null, request.name(), request.email(), request.phone(), request.password());
    }

    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }
}

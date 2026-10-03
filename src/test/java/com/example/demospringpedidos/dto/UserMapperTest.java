package com.example.demospringpedidos.dto;

import com.example.demospringpedidos.entities.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    void mapsRequestPasswordIntoUserEntity() {
        UserRequestDto request = new UserRequestDto(
                "Maria", "maria@example.com", "999999999", "Abcdefg1");

        User user = mapper.toEntity(request);

        assertEquals("Abcdefg1", user.getPassword());
    }

    @Test
    void mapsUserToResponseWithoutPassword() {
        User user = new User(1L, "Maria", "maria@example.com", "999999999", "Abcdefg1");

        UserResponseDto response = mapper.toResponse(user);

        assertEquals(new UserResponseDto(1L, "Maria", "maria@example.com", "999999999"), response);
        assertFalse(java.util.Arrays.stream(UserResponseDto.class.getRecordComponents())
                .anyMatch(component -> component.getName().equals("password")));
    }
}

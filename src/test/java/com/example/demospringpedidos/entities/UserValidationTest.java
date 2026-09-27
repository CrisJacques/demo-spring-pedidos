package com.example.demospringpedidos.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsUserWithValidFields() {
        User user = new User(null, "Maria", "maria@example.com", "999123456", "Abcdefg1");

        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void rejectsBlankRequiredFields() {
        User user = new User(null, " ", "", null, "\t");

        assertInvalidField(user, "name", "Field is required");
        assertInvalidField(user, "email", "Field is required");
        assertInvalidField(user, "phone", "Field is required");
        assertInvalidField(user, "password", "Field is required");
    }

    @Test
    void rejectsMalformedEmail() {
        assertInvalidField(
                new User(null, "Maria", "not-an-email", "999123456", "Abcdefg1"),
                "email",
                "Not valid email"
        );
    }

    @Test
    void rejectsPhoneWithNonNumericCharacters() {
        assertInvalidField(
                new User(null, "Maria", "maria@example.com", "999-123", "Abcdefg1"),
                "phone",
                "Phone must contain only numbers"
        );
    }

    @Test
    void rejectsPasswordsWithoutRequiredLengthOrCharacterClasses() {
        assertInvalidField(
                new User(null, "Maria", "maria@example.com", "999123456", "Abcdef1"),
                "password",
                "The password must be at least 8 characters long, including a number, an uppercase letter, and a lowercase letter."
        );
        assertInvalidField(
                new User(null, "Maria", "maria@example.com", "999123456", "abcdefg1"),
                "password",
                "The password must be at least 8 characters long, including a number, an uppercase letter, and a lowercase letter."
        );
        assertInvalidField(
                new User(null, "Maria", "maria@example.com", "999123456", "ABCDEFG1"),
                "password",
                "The password must be at least 8 characters long, including a number, an uppercase letter, and a lowercase letter."
        );
        assertInvalidField(
                new User(null, "Maria", "maria@example.com", "999123456", "Abcdefgh"),
                "password",
                "The password must be at least 8 characters long, including a number, an uppercase letter, and a lowercase letter."
        );
    }

    private void assertInvalidField(User user, String field, String message) {
        assertTrue(validator.validate(user).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals(field)
                        && error.getMessage().equals(message)));
    }
}

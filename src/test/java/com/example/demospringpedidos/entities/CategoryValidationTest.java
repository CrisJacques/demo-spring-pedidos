package com.example.demospringpedidos.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsCategoryWithName() {
        assertTrue(validator.validate(new Category(null, "Books")).isEmpty());
    }

    @Test
    void rejectsMissingOrBlankName() {
        assertInvalidName(new Category());
        assertInvalidName(new Category(null, "  "));
    }

    private void assertInvalidName(Category category) {
        assertTrue(validator.validate(category).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("name")
                        && error.getMessage().equals("Field is required")));
    }
}

package com.example.demospringpedidos.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsProductWithValidFields() {
        Product product = new Product(null, "Book", "A useful book", 10.0, "");

        assertTrue(validator.validate(product).isEmpty());
    }

    @Test
    void acceptsValidProductWithIdAndCategoriesForUpdate() {
        Product product = new Product(1L, "Book", "A useful book", 10.0, "");
        product.getCategories().add(new Category(2L, "Books"));

        assertTrue(validator.validate(product).isEmpty());
    }

    @Test
    void rejectsMissingOrBlankNameAndDescription() {
        assertInvalidField(new Product(null, null, "Description", 10.0, ""),
                "name", "Field is required");
        assertInvalidField(new Product(null, "  ", "Description", 10.0, ""),
                "name", "Field is required");
        assertInvalidField(new Product(null, "Book", null, 10.0, ""),
                "description", "Field is required");
        assertInvalidField(new Product(null, "Book", "\t", 10.0, ""),
                "description", "Field is required");
    }

    @Test
    void rejectsMissingPrice() {
        assertInvalidField(new Product(null, "Book", "Description", null, ""),
                "price", "Field can not be null");
    }

    @Test
    void rejectsZeroOrNegativePrice() {
        assertInvalidField(new Product(null, "Book", "Description", 0.0, ""),
                "price", "Price must be greater than zero");
        assertInvalidField(new Product(null, "Book", "Description", -1.0, ""),
                "price", "Price must be greater than zero");
    }

    private void assertInvalidField(Product product, String field, String message) {
        assertTrue(validator.validate(product).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals(field)
                        && error.getMessage().equals(message)));
    }
}

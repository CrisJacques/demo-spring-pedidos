package com.example.demospringpedidos.resources;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryResourceRestAssuredTest {
    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/categories";
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void getAllCategoriesReturnsCategories() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", hasItems("Electronics", "Books", "Computers"));
    }

    @Test
    void getCategoryByIdReturnsCategory() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("name", equalTo("Electronics"));
    }

    @Test
    void getCategoryByIdReturnsNotFoundWhenCategoryDoesNotExist() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 999999)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/categories/999999"));
    }

    @Test
    void postCategoryCreatesCategoryAndReturnsLocation() {
        String name = uniqueCategoryName();

        given()
                .contentType(ContentType.JSON)
                .body(categoryJson(name))
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", matchesPattern(".*/categories/\\d+"))
                .body("id", greaterThan(0))
                .body("name", equalTo(name));
    }

    @Test
    void postCategoryRejectsMissingName() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("name: Field is required"))
                .body("path", equalTo("/categories"));
    }

    @Test
    void postCategoryRejectsBlankName() {
        given()
                .contentType(ContentType.JSON)
                .body(categoryJson("  "))
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("name: Field is required"))
                .body("path", equalTo("/categories"));
    }

    @Test
    void postCategoryRejectsDuplicateName() {
        given()
                .contentType(ContentType.JSON)
                .body(categoryJson("Electronics"))
                .when()
                .post()
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("Category already exists"))
                .body("path", equalTo("/categories"));
    }

    @Test
    void putCategoryUpdatesExistingCategory() {
        int categoryId = createCategory();
        String updatedName = uniqueCategoryName();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", categoryId)
                .body(categoryJson(updatedName))
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(categoryId))
                .body("name", equalTo(updatedName));
    }

    @Test
    void putCategoryReturnsNotFoundWhenCategoryDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 999999)
                .body(categoryJson(uniqueCategoryName()))
                .when()
                .put("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/categories/999999"));
    }

    @Test
    void putCategoryRejectsBlankName() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(categoryJson(" "))
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("name: Field is required"))
                .body("path", equalTo("/categories/1"));
    }

    @Test
    void putCategoryRejectsMissingName() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body("{}")
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("name: Field is required"))
                .body("path", equalTo("/categories/1"));
    }

    @Test
    void putCategoryRejectsDuplicateName() {
        int categoryId = createCategory();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", categoryId)
                .body(categoryJson("Books"))
                .when()
                .put("/{id}")
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("New name for category already exists"))
                .body("path", equalTo("/categories/" + categoryId));
    }

    @Test
    void deleteCategoryRemovesExistingCategory() {
        int categoryId = createCategory();

        given()
                .pathParam("id", categoryId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204)
                .body(emptyString());

        given()
                .pathParam("id", categoryId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteCategoryReturnsNotFoundWhenCategoryDoesNotExist() {
        given()
                .pathParam("id", 999999)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/categories/999999"));
    }

    @Test
    void deleteCategoryReturnsBadRequestWhenProductsAreAssociated() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Database error"))
                .body("message", equalTo("Category has associated products"))
                .body("path", equalTo("/categories/1"));
    }

    private int createCategory() {
        return given()
                .contentType(ContentType.JSON)
                .body(categoryJson(uniqueCategoryName()))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String uniqueCategoryName() {
        return "RestAssured Category %s".formatted(UUID.randomUUID());
    }

    private String categoryJson(String name) {
        return """
                {"name":"%s"}
                """.formatted(name);
    }

    @AfterEach
    void resetRestAssured() {
        RestAssured.reset();
    }
}

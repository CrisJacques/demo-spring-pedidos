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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserResourceRestAssuredTest {
    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/users";
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void getAllUsersReturnsPublicUserDataWithoutPasswords() {
        String responseBody = given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("name", hasItems("Maria Brown", "Alex Green"))
                .extract()
                .asString();

        assertFalse(responseBody.contains("\"password\""));
    }

    @Test
    void getUserByIdReturnsPublicUserDataWithoutPassword() {
        String responseBody = given()
                .accept(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("name", equalTo("Maria Brown"))
                .body("email", equalTo("maria@gmail.com"))
                .extract()
                .asString();

        assertFalse(responseBody.contains("\"password\""));
    }

    @Test
    void getUserByIdReturnsNotFoundWhenUserDoesNotExist() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 999999)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void postUserCreatesUserAndReturnsLocationWithoutPassword() {
        String email = "restassured-%s@example.com".formatted(UUID.randomUUID());
        String responseBody = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "RestAssured User",
                          "email": "%s",
                          "phone": "999999999",
                          "password": "Abcdefg1"
                        }
                        """.formatted(email))
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", matchesPattern(".*/users/\\d+"))
                .body("name", equalTo("RestAssured User"))
                .body("email", equalTo(email))
                .extract()
                .asString();

        assertFalse(responseBody.contains("\"password\""));
    }

    @Test
    void postUserRejectsInvalidEmail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "RestAssured User",
                          "email": "invalid-email",
                          "phone": "999999999",
                          "password": "Abcdefg1"
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("message", equalTo("email: Not valid email"));
    }

    @Test
    void postUserRejectsDuplicateEmail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "Duplicate User",
                          "email": "maria@gmail.com",
                          "phone": "999999999",
                          "password": "Abcdefg1"
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(422)
                .body("message", equalTo("Email already exists."));
    }

    @Test
    void putUserUpdatesExistingUserAndDoesNotReturnPassword() {
        int userId = createUser(
                "Original User",
                "restassured-%s@example.com".formatted(UUID.randomUUID()));
        String updatedEmail = "restassured-%s@example.com".formatted(UUID.randomUUID());

        String responseBody = given()
                .contentType(ContentType.JSON)
                .pathParam("id", userId)
                .body("""
                        {
                          "name": "Updated User",
                          "email": "%s",
                          "phone": "888888888",
                          "password": "Abcdefg2"
                        }
                        """.formatted(updatedEmail))
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userId))
                .body("name", equalTo("Updated User"))
                .body("email", equalTo(updatedEmail))
                .body("phone", equalTo("888888888"))
                .extract()
                .asString();

        assertFalse(responseBody.contains("\"password\""));
    }

    @Test
    void putUserReturnsNotFoundWhenUserDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 999999)
                .body("""
                        {
                          "name": "Updated User",
                          "email": "updated@example.com",
                          "phone": "888888888",
                          "password": "Abcdefg2"
                        }
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void putUserRejectsInvalidEmail() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body("""
                        {
                          "name": "Updated User",
                          "email": "invalid-email",
                          "phone": "888888888",
                          "password": "Abcdefg2"
                        }
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("message", equalTo("email: Not valid email"));
    }

    @Test
    void deleteUserRemovesExistingUser() {
        int userId = createUser(
                "User To Delete",
                "restassured-%s@example.com".formatted(UUID.randomUUID()));

        given()
                .pathParam("id", userId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204)
                .body(emptyString());

        given()
                .pathParam("id", userId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteUserReturnsNotFoundWhenUserDoesNotExist() {
        given()
                .pathParam("id", 999999)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404);
    }

    private int createUser(String name, String email) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "%s",
                          "email": "%s",
                          "phone": "999999999",
                          "password": "Abcdefg1"
                        }
                        """.formatted(name, email))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @AfterEach
    void resetRestAssured() {
        RestAssured.reset();
    }
}

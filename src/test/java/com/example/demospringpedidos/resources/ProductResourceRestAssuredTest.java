package com.example.demospringpedidos.resources;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductResourceRestAssuredTest {
    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/products";
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void getAllProductsReturnsProducts() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", hasItems("The Lord of the Rings", "Smart TV", "Macbook Pro"));
    }

    @Test
    void getProductByIdReturnsProduct() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("name", equalTo("The Lord of the Rings"))
                .body("price", equalTo(90.5f));
    }

    @Test
    void getProductByIdReturnsNotFoundWhenProductDoesNotExist() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 999999)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/products/999999"));
    }

    @Test
    void postProductCreatesProductAndReturnsLocation() {
        String name = uniqueProductName();

        given()
                .contentType(ContentType.JSON)
                .body(productJson(name, "New product description", 2500.0))
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", matchesPattern(".*/products/\\d+"))
                .body("id", greaterThan(0))
                .body("name", equalTo(name))
                .body("description", equalTo("New product description"))
                .body("price", equalTo(2500.0f));
    }

    @Test
    void postProductRejectsMissingRequiredFields() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("name: Field is required"))
                .body("message", containsString("description: Field is required"))
                .body("message", containsString("price: Field can not be null"))
                .body("path", equalTo("/products"));
    }

    @Test
    void postProductRejectsBlankNameAndDescription() {
        given()
                .contentType(ContentType.JSON)
                .body(productJson(" ", " ", 10.0))
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("name: Field is required"))
                .body("message", containsString("description: Field is required"))
                .body("path", equalTo("/products"));
    }

    @Test
    void postProductRejectsNullPrice() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name":"Product","description":"Description","price":null}
                        """)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("price: Field can not be null"))
                .body("path", equalTo("/products"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0})
    void postProductRejectsZeroOrNegativePrice(double price) {
            given()
                    .contentType(ContentType.JSON)
                    .body(productJson(uniqueProductName(), "Description", price))
                    .when()
                    .post()
                    .then()
                    .statusCode(400)
                    .body("error", equalTo("Validation error"))
                    .body("message", equalTo("price: Price must be greater than zero"))
                    .body("path", equalTo("/products"));
    }

    @Test
    void postProductRejectsDuplicateName() {
        given()
                .contentType(ContentType.JSON)
                .body(productJson("The Lord of the Rings", "Description", 10.0))
                .when()
                .post()
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("Product already exists."))
                .body("path", equalTo("/products"));
    }

    @Test
    void putProductUpdatesExistingProduct() {
        int productId = createProduct();
        String updatedName = uniqueProductName();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", productId)
                .body(productJson(updatedName, "Updated description", 2800.0))
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(productId))
                .body("name", equalTo(updatedName))
                .body("description", equalTo("Updated description"))
                .body("price", equalTo(2800.0f));
    }

    @Test
    void putProductReturnsNotFoundWhenProductDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 999999)
                .body(productJson(uniqueProductName(), "Description", 10.0))
                .when()
                .put("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/products/999999"));
    }

    @Test
    void putProductRejectsMissingFields() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body("{}")
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("name: Field is required"))
                .body("message", containsString("description: Field is required"))
                .body("message", containsString("price: Field can not be null"))
                .body("path", equalTo("/products/1"));
    }

    @Test
    void putProductRejectsZeroPrice() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(productJson(uniqueProductName(), "Description", 0.0))
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", equalTo("price: Price must be greater than zero"))
                .body("path", equalTo("/products/1"));
    }

    @Test
    void putProductRejectsDuplicateName() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(productJson("Smart TV", "Description", 10.0))
                .when()
                .put("/{id}")
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("New product name already exists."))
                .body("path", equalTo("/products/1"));
    }

    @Test
    void deleteProductRemovesExistingProduct() {
        int productId = createProduct();

        given()
                .pathParam("id", productId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204)
                .body(emptyString());

        given()
                .pathParam("id", productId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteProductReturnsNotFoundWhenProductDoesNotExist() {
        given()
                .pathParam("id", 999999)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/products/999999"));
    }

    @Test
    void deleteProductReturnsBadRequestWhenProductHasAssociatedOrders() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Database error"))
                .body("message", equalTo("Product has associated orders."))
                .body("path", equalTo("/products/1"));
    }

    private int createProduct() {
        return given()
                .contentType(ContentType.JSON)
                .body(productJson(uniqueProductName(), "Product created for API test", 10.0))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String uniqueProductName() {
        return "RestAssured Product %s".formatted(UUID.randomUUID());
    }

    private String productJson(String name, String description, double price) {
        return """
                {"name":"%s","description":"%s","price":%s,"imgUrl":""}
                """.formatted(name, description, price);
    }

    @AfterEach
    void resetRestAssured() {
        RestAssured.reset();
    }
}

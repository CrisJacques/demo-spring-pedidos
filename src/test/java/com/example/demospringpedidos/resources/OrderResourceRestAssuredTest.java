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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderResourceRestAssuredTest {
    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/orders";
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    void getAllOrdersReturnsOrders() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", hasItem(1))
                .body("orderStatus", hasItem("PAID"));
    }

    @Test
    void getOrderByIdReturnsOrder() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 1)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("orderStatus", equalTo("PAID"))
                .body("client.name", equalTo("Maria Brown"))
                .body("items.size()", greaterThan(0));
    }

    @Test
    void getOrderByIdReturnsNotFoundWhenOrderDoesNotExist() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", 999999)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders/999999"));
    }

    @Test
    void postOrderCreatesOrderAndReturnsLocation() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clientId": 1,
                          "items": [{"productId": 1, "quantity": 2}]
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", matchesPattern(".*/orders/\\d+"))
                .body("id", greaterThan(0))
                .body("orderStatus", equalTo("WAITING_PAYMENT"))
                .body("client.id", equalTo(1))
                .body("items[0].product.id", equalTo(1))
                .body("items[0].quantity", equalTo(2))
                .body("total", equalTo(181.0f));
    }

    @Test
    void postOrderRejectsMissingRequiredFields() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("clientId: Client id is required."))
                .body("message", containsString("items: At least one item is required."))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderRejectsQuantityZero() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clientId": 1,
                          "items": [{"productId": 1, "quantity": 0}]
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items[0].quantity: Item quantity must be greater than zero."))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderRejectsMissingProductId() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": 1, "items": [{"quantity": 1}]}
                        """)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items[0].productId: Product id is required for each item."))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderRejectsMissingQuantity() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": 1, "items": [{"productId": 1}]}
                        """)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items[0].quantity: Item quantity must be greater than zero."))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderReturnsNotFoundWhenClientDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clientId": 999999,
                          "items": [{"productId": 1, "quantity": 1}]
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderReturnsNotFoundWhenProductDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clientId": 1,
                          "items": [{"productId": 999999, "quantity": 1}]
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders"));
    }

    @Test
    void postOrderRejectsQuantityOverflowAsBusinessError() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clientId": 1,
                          "items": [
                            {"productId": 1, "quantity": 2147483647},
                            {"productId": 1, "quantity": 1}
                          ]
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("Total quantity for a product exceeds the supported limit."))
                .body("path", equalTo("/orders"));
    }

    @Test
    void putOrderUpdatesExistingOrder() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {
                          "orderStatus": 2,
                          "items": [{"productId": 1, "quantity": 3}]
                        }
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(2))
                .body("orderStatus", equalTo("PAID"))
                .body("items.size()", equalTo(1))
                .body("items[0].product.id", equalTo(1))
                .body("items[0].quantity", equalTo(3));
    }

    @Test
    void putOrderReturnsNotFoundWhenOrderDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 999999)
                .body("""
                        {"orderStatus": 1, "items": [{"productId": 1, "quantity": 1}]}
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders/999999"));
    }

    @Test
    void putOrderRejectsEmptyListItems() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {"orderStatus": 1, "items": []}
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items: At least one item is required."))
                .body("path", equalTo("/orders/2"));
    }

    @Test
    void putOrderRejectsZeroQuantity() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {"orderStatus": 1, "items": [{"productId": 1, "quantity": 0}]}
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items[0].quantity: Item quantity must be greater than zero."))
                .body("path", equalTo("/orders/2"));
    }

    @Test
    void putOrderRejectsMissingProductId() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {"orderStatus": 1, "items": [{"quantity": 1}]}
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation error"))
                .body("message", containsString("items[0].productId: Product id is required for each item."))
                .body("path", equalTo("/orders/2"));
    }

    @Test
    void putOrderReturnsNotFoundWhenProductDoesNotExist() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {"orderStatus": 1, "items": [{"productId": 999999, "quantity": 1}]}
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders/2"));
    }

    @Test
    void putOrderRejectsQuantityOverflowAsBusinessError() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body("""
                        {
                          "orderStatus": 1,
                          "items": [
                            {"productId": 1, "quantity": 2147483647},
                            {"productId": 1, "quantity": 1}
                          ]
                        }
                        """)
                .when()
                .put("/{id}")
                .then()
                .statusCode(422)
                .body("error", equalTo("Business error"))
                .body("message", equalTo("Total quantity for a product exceeds the supported limit."))
                .body("path", equalTo("/orders/2"));
    }

    @Test
    void deleteOrderRemovesExistingOrder() {
        int orderId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": 1, "items": [{"productId": 1, "quantity": 1}]}
                        """)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .pathParam("id", orderId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", orderId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteOrderReturnsNotFoundWhenOrderDoesNotExist() {
        given()
                .pathParam("id", 999999)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Resource not found"))
                .body("path", equalTo("/orders/999999"));
    }

    @AfterEach
    void resetRestAssured() {
        RestAssured.reset();
    }
}

package com.example.productapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class ProductResourceTest {
    private static final String TOKEN_ENDPOINT = "http://localhost:8080/realms/dev-realm/protocol/openid-connect/token";

    @Test
    void getAccessTokenWithUsernameAndPassword() {
        given()
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("client_id", "api-test-client")
                .formParam("username", "juan_user")
                .formParam("password", "welcome1")
                .post(TOKEN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("access_token", not(emptyOrNullString()));
    }

    @Test
    void accessDeniedWithoutToken() {
        given()
                .get("/api/products")
                .then()
                .statusCode(401);
    }

    @Test
    void readWithUserRole() {
        given()
                .auth().oauth2(accessToken("juan_user", "welcome1"))
                .get("/api/products")
                .then()
                .statusCode(200);
    }

    @Test
    void writeDeniedToUserRole() {
        String token = accessToken("juan_user", "welcome1");
        Map<String, Object> product = product("USER-DENIED-" + UUID.randomUUID());

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(product)
                .post("/api/products")
                .then()
                .statusCode(403);

        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(product)
                .put("/api/products/1")
                .then()
                .statusCode(403);

        given()
                .auth().oauth2(token)
                .delete("/api/products/1")
                .then()
                .statusCode(403);
    }

    @Test
    void adminCanPerformAllCrudOperations() {
        String token = accessToken("alice_admin", "welcome2");
        String sku = "ADMIN-CRUD-" + UUID.randomUUID();
        String id = given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(product(sku))
                .post("/api/products")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("id");

        try {
            given()
                    .auth().oauth2(token)
                    .get("/api/products/{id}", id)
                    .then()
                    .statusCode(200)
                    .body("sku", equalTo(sku));

            given()
                    .auth().oauth2(token)
                    .get("/api/products")
                    .then()
                    .statusCode(200)
                    .body("sku", hasItem(sku));

            given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "nombre", "Updated test product",
                            "precio", 24.50,
                            "stock", 7,
                            "sku", sku))
                    .put("/api/products/{id}", id)
                    .then()
                    .statusCode(200)
                    .body("nombre", equalTo("Updated test product"));

            given()
                    .auth().oauth2(token)
                    .delete("/api/products/{id}", id)
                    .then()
                    .statusCode(204);

            given()
                    .auth().oauth2(token)
                    .get("/api/products/{id}", id)
                    .then()
                    .statusCode(404);
        } finally {
            given()
                    .auth().oauth2(token)
                    .delete("/api/products/{id}", id);
        }
    }

    private static String accessToken(String username, String password) {
        return given()
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("client_id", "api-test-client")
                .formParam("username", username)
                .formParam("password", password)
                .post(TOKEN_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

    private static Map<String, Object> product(String sku) {
        return Map.of(
                "nombre", "Test product",
                "precio", 19.95,
                "stock", 4,
                "sku", sku);
    }
}
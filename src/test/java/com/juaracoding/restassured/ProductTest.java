package com.juaracoding.restassured;

import static org.hamcrest.Matchers.*;

import org.hamcrest.CoreMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class ProductTest {
    private RequestSpecification requestSpecification;
    private Response response;
    private ValidatableResponse validatableResponse;
    private final String BASE_URL = "https://fakestoreapi.com";


    @BeforeTest
    public void setup(){
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void getAllTest() {
       

        requestSpecification = RestAssured.given();
        response = requestSpecification.get("/products");

        // response.prettyPrint();

        //cara pertama validasi pakai restAssured
        validatableResponse = response.then();
        validatableResponse.statusCode(200);
        validatableResponse.statusLine("HTTP/1.1 200 OK");

        //cara kedua validasi pakai testNG
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK");

    }

    //Get product details
    @Test(priority = 2)
    public void getSingleProductTest() {

        RestAssured.given()
            .pathParam("id", 2)
        .when()
            .get("/products/{id}")
        .then()
            .statusCode(200)
            .statusLine("HTTP/1.1 200 OK")
            .body("id", equalTo(2))
            .body("title", notNullValue())
            .body("price", greaterThan(0F))
            .body("category", notNullValue());
    }

    //Update product details
    @Test(priority = 3)
    public void updateProductTest() {

        String requestBody = "{\n" +
                "  \"title\": \"Updated Product QA\",\n" +
                "  \"price\": 99.99,\n" +
                "  \"description\": \"Updated by automation test\",\n" +
                "  \"image\": \"https://i.pravatar.cc\",\n" +
                "  \"category\": \"electronics\"\n" +
                "}";

        response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .pathParam("id", 2)
            .when()
                .put("/products/{id}");

        response.prettyPrint();

        validatableResponse = response.then();

        // RestAssured validation
        validatableResponse.statusCode(200);
        validatableResponse.body("title", CoreMatchers.equalTo("Updated Product QA"));
        validatableResponse.body("price", CoreMatchers.equalTo(99.99F));

        // TestNG validation
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("title"), "Updated Product QA");
    }

    //Delete product
    @Test(priority = 4)
    public void deleteProductTest() {

        response = RestAssured
            .given()
                .pathParam("id", 2)
            .when()
                .delete("/products/{id}");

        response.prettyPrint();

        validatableResponse = response.then();

        // RestAssured validation
        validatableResponse.statusCode(200);
        validatableResponse.body("id", CoreMatchers.equalTo(2));

        // TestNG validation
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("id"), 2);
    }


}


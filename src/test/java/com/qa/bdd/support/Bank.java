package com.qa.bdd.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.math.BigDecimal;

/** The one place that knows how to talk to the bank. Steps stay readable. */
public final class Bank {

    public static final int SEEDED_CUSTOMER_ID = 12212;
    public static final int FIRST_ACCOUNT = 12567;
    public static final int SECOND_ACCOUNT = 12789;
    public static final int MISSING_ACCOUNT = 99999999;

    private static final String DEFAULT_BASE =
            "http://localhost:8081/parabank/services/bank";

    private final RequestSpecification api;

    public Bank() {
        String base = System.getenv().getOrDefault("BASE_URL", DEFAULT_BASE);
        RestAssured.baseURI = base;
        this.api = new RequestSpecBuilder().setBaseUri(base).setAccept(ContentType.JSON).build();
    }

    public Response account(int id) {
        return RestAssured.given().spec(api).when().get("/accounts/" + id);
    }

    public BigDecimal balanceOf(int id) {
        return new BigDecimal(account(id).then().statusCode(200).extract().path("balance").toString());
    }

    public Response transfer(int from, int to, String amount) {
        return RestAssured.given().spec(api)
                .queryParam("fromAccountId", from)
                .queryParam("toAccountId", to)
                .queryParam("amount", amount)
                .when().post("/transfer");
    }

    public Response accountsOf(int customerId) {
        return RestAssured.given().spec(api).when().get("/customers/" + customerId + "/accounts");
    }
}

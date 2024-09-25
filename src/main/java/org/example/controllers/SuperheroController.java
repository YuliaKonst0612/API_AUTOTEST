package org.example.controllers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.models.Superhero;
import org.hamcrest.Matchers;

import static org.example.constants.SuperheroConstant.DEFAULT_SUPERHERO;

public class SuperheroController {
    private String baseUrl = "https://superhero.qa-test.csssr.com/superheroes";

    private RequestSpecification requestSpecification;

    public SuperheroController() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.defaultParser = Parser.JSON;
    }

    public Response addNewHero(Superhero superhero) {
        Response response = RestAssured.given(requestSpecification)
                .body(superhero)
                .post();
        return response;
    }

    public Response addDefaultHero() {
        Response response = RestAssured.given(requestSpecification)
                .body(DEFAULT_SUPERHERO)
                .post();
        return response;
    }
}

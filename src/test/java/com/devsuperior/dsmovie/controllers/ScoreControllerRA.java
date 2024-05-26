package com.devsuperior.dsmovie.controllers;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ScoreControllerRA {

    @SuppressWarnings("unused")
    private Long nonexistmovieId, existingmovieId;

    private Map<String, Object> putMovieInstance;


    @BeforeEach
    public void setUp() throws JSONException {
        baseURI = "http://localhost:8080";

        existingmovieId = 2L;
        nonexistmovieId = 30L;

        putMovieInstance = new HashMap<>();
        putMovieInstance.put("title", "O Espetacular Homem-Aranha 2: A Ameaça de Electro");
        putMovieInstance.put("count", 0);
        putMovieInstance.put("score", 0.0f);
        putMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/u7SeO6Y42P7VCTWLhpnL96cyOqd.jpg");


    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

        JSONObject score = new JSONObject(putMovieInstance);
        nonexistmovieId = 30L;


        given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(score)
                .when()
                .put("/scores{id}", nonexistmovieId)
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

        JSONObject score = new JSONObject(putMovieInstance);
        existingmovieId = 2L;

        given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(score)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("errors[0].fieldName", equalTo("movieId"))
                .body("errors[0].message", equalTo("Campo requerido"))
                .body("status", is(422));
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {

        putMovieInstance.put("score", -1.0);
        putMovieInstance.put("movieId", 2L);
        JSONObject score = new JSONObject(putMovieInstance);


        given()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(score)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("errors[0].fieldName", equalTo("score"))
                .body("errors[0].message", equalTo("Valor mínimo 0"))
                .body("status", is(422));
    }
}


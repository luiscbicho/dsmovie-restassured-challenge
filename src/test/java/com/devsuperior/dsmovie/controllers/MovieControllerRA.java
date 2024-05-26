package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class MovieControllerRA {

    private String adminUsername, adminPassword, clientusername, clientpassword;

    private Long existingMovieId, nonExistingMovieId;

    private String adminToken, clientToken, invalidToken;

    private String moviesTitle;

    private Map<String, Object> postMovieInstance;


    @BeforeEach
    public void setUp() throws JSONException {
        baseURI = "http://localhost:8080";

        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
        clientusername = "alex@gmail.com";
        clientpassword = "123456";

        existingMovieId = 1L;
        nonExistingMovieId = 30L;

        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        clientToken = TokenUtil.obtainAccessToken(clientusername, adminPassword);
        invalidToken = adminToken + "xpto";

        moviesTitle = "Harry Potter e as Relíquias da Morte - Parte 1";

        postMovieInstance = new HashMap<>();
        postMovieInstance.put("title", "Teste Movie");
        postMovieInstance.put("count", 0);
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");


    }

    @Test
    public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

        given()
                .when()
                .get("/movies")
                .then()
                .statusCode(200);

    }

    @Test
    public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
        given()
                .get("/movies?title={moviesTitle}", moviesTitle)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(19))
                .body("content.title[0]", equalTo("Harry Potter e as Relíquias da Morte - Parte 1"))
                .body("content.score[0]", is(0.0F))
                .body("content.count[0]", is(0))
                .body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vcrgU0KaNj5mKUCIQSUdiQwTE9y.jpg"));
    }

    @Test
    public void findByIdShouldReturnMovieWhenIdExists() {

        given()
                .get("/movies/{id}", existingMovieId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("title", equalTo("The Witcher"))
                .body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
                .body("score", is(4.5F))
                .body("count", is(2));

    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

        given()
                .get("/movies/{id}", nonExistingMovieId)
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("error", equalTo("Recurso não encontrado"))
                .body("path", equalTo("/movies/30"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
        postMovieInstance.put("title", "");

        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(422)
                .body("errors.message", hasItems("Tamanho deve ser entre 5 e 80 caracteres", "Campo requerido"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(401);
    }


}
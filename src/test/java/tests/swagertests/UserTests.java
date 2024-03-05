package tests.swagertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.swager.FullUser;
import models.swager.Info;
import models.swager.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserTests {
    private static Random random;
    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        random = new Random();

    }

    @Test
    public void positiveRegisterTest(){

        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("lenyaautotester" + randomNumber)
                .pass("bredoviyteam")
                .build();

        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("User created", info.getMessage());
    }


    @Test
    public void negativeRegisterLoginTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("lenyaautotester" + randomNumber)
                .pass("bredoviyteam")
                .build();

        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Info errorInfo = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("Login already exist", errorInfo.getMessage());

    }

@Test
    public void positiveAdminAuthTest(){

    JwtAuthData authData = new JwtAuthData("admin","admin");
    given().contentType(ContentType.JSON)
            .body(authData)
            .post("api/login")
            .then().statusCode(200)
            .extract().jsonPath().getString("token");
}





}

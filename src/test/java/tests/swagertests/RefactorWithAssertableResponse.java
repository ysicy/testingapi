package tests.swagertests;

import assertions.AssertableResponse;

import assertions.GenericAssertableResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.swager.FullUser;
import models.swager.Info;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatuscode;
import static io.restassured.RestAssured.given;

public class RefactorWithAssertableResponse {
    private static Random random;
//    private static UserService userService;
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        random = new Random();


    }
    @Test
    public void negativeRegisterPasswordTest() {

        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("lenyaautotester" + randomNumber)
                .build();

        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        new AssertableResponse(given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then())
                .should(hasMessage("Missing login or password"))
                .should(hasStatuscode(400));

        new GenericAssertableResponse<Info>(given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then(), new TypeRef<Info>() {})
                .should(hasMessage("Missing login or password"))
                .should(hasStatuscode(400))
                .asObject();

        Assertions.assertEquals("Missing login or password", info.getMessage());


    }
}

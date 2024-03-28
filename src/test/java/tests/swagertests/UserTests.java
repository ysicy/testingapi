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
import models.swager.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatuscode;
import static io.restassured.RestAssured.given;

public class UserTests {
    private static Random random;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        random = new Random();

    }

    @Test
    public void positiveRegisterTest() {

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
    public void negativeRegisterLoginTest() {
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
    public void positiveAdminAuthTest() {

        JwtAuthData authData = new JwtAuthData("admin", "admin");
        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    @Test
    public void positiveNewUserAuthTest() {

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

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
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
    @Test
    public void negativeAuthTest() {
        JwtAuthData authData = new JwtAuthData("sdasdasdas3242", "312312dasfsdfas");

        given().contentType(ContentType.JSON)
                .body(authData)
                .post("api/login")
                .then().statusCode(401);


    }

@Test
    public void positiveGetUserInfoTest(){

    JwtAuthData authData = new JwtAuthData("admin", "admin");
    String token = given().contentType(ContentType.JSON)
            .body(authData)
            .post("api/login")
            .then().statusCode(200)
            .extract().jsonPath().getString("token");

    Assertions.assertNotNull(token);

    given().auth().oauth2(token)
            .get("/api/user")
            .then().statusCode(200);
}


    @Test
    public void positiveGetUserInfoInvalidJwtTest(){



        given().auth().oauth2("tokenepti")
                .get("/api/user")
                .then().statusCode(401);
    }

@Test
    public void negativeGetUserInfoWithoutJwtTest(){

    given()
            .get("/api/user")
            .then().statusCode(401);
}

@Test
    public void positiveChangedUserPassTest(){
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

    JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());
    String token = given().contentType(ContentType.JSON)
            .body(authData)
            .post("api/login")
            .then().statusCode(200)
            .extract().jsonPath().getString("token");


    Map<String, String> password = new HashMap<>();
    String updatedPassValue = "passiveness";
    password.put("password", updatedPassValue);
   Info updatedPassInfo =  given().contentType(ContentType.JSON)
            .auth().oauth2(token)
            .body(password)
            .put("api/user")
            .then().extract().jsonPath().getObject("info", Info.class);

   Assertions.assertEquals("User password successfully changed",updatedPassInfo.getMessage());

    JwtAuthData updatedAuthData = new JwtAuthData(user.getLogin(), updatedPassValue);
    token = given().contentType(ContentType.JSON)
            .body(updatedAuthData)
            .post("api/login")
            .then().statusCode(200)
            .extract().jsonPath().getString("token");


    given().auth().oauth2(token)
            .get("/api/user")
            .then().statusCode(200);

}





}



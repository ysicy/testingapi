package tests.api;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

public class ApiRefactoredTests {

    @BeforeAll
    public static void setUp(){

        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new ResponseLoggingFilter(),new RequestLoggingFilter() );
    }


    @Test
    public void getAllUsersTest(){
        given().get("/users")
                .then()
                .statusCode(200);

    }

}

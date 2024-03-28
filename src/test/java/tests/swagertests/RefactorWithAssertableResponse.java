package tests.swagertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import models.swager.FullUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatuscode;

public class RefactorWithAssertableResponse {
    private static Random random;
    private static UserService userService;
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        random = new Random();


    }
    private FullUser getRandomUser(){
        int randomNumber = Math.abs(random.nextInt());
        return FullUser.builder()
                .login("lenyaautotester" + randomNumber)
                .pass("bredoviyteam")
                .build();

    }


    @Test
    public void positiveRegisterTest() {

        FullUser user = getRandomUser();
        userService.register(user)
                .should(hasStatuscode(201))
                .should(hasMessage("User created"));

    }


    @Test
    public void negativeRegisterLoginTest() {

        FullUser user = getRandomUser();
        userService.register(user);
        userService.register(user)
                .should(hasStatuscode(400))
                .should(hasMessage("Login already exist"));

    }
    @Test
    public void negativeRegisterPasswordTest() {

        FullUser user = getRandomUser();
      user.setPass(null);
        userService.register(user)
                .should(hasStatuscode(400))
                .should(hasMessage("Missing login or password"));
    }

}

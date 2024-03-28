package models.services;

import assertions.AssertableResponse;
import io.restassured.http.ContentType;
import models.swager.FullUser;

import static io.restassured.RestAssured.given;

public class UserSevice {

    public AssertableResponse register(FullUser user){
        return new AssertableResponse(given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then());

    }

    public AssertableResponse getUserInfo (String jwt){
        return new AssertableResponse(given().auth().oauth2(jwt)
                .get("/api/user")
                .then());
    }


}

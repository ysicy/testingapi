package models.services;

import assertions.AssertableResponse;

import static io.restassured.RestAssured.given;

public class FileService {
    public AssertableResponse downloadBaseImage(){
        return new AssertableResponse(given().get("/api/files/download").then());

    }

    public AssertableResponse downloadLastUploaded(){
        return new AssertableResponse(given().get("/api/files/downloadLastUploaded").then());
    }


    public AssertableResponse uploadFile(){
        return new AssertableResponse(given().post("/api/files/upload").then());
    }
}

package tests.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.fakeapiuser.Address;
import models.fakeapiuser.Geolocation;
import models.fakeapiuser.Name;
import models.fakeapiuser.UserRoot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestApi {

    @Test
    public void getAllUsersTest(){
        given().get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200);

    }


    @Test
    public void getSingleUserTest(){
        int userId = 5;
        given().pathParam("userId", userId)
                .get("https://fakestoreapi.com/users/{userId}")
                .then().log().all()
                .body("id", equalTo(userId))
                .body("address.zipcode", matchesPattern("\\d{5}-\\d{4}"));


    }

    @Test
    public void getAllUsersWithLimitTest(){
        int limitSize = 3;
        given().queryParam("limit",limitSize)
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(limitSize));
    }
    @Test
    public void getOnlyUserWithLimitTest(){
        int limitSize = 4;
        given().queryParam("limit",limitSize)
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200)
                .body("", hasSize(limitSize));
    }
//какие то ашипки сыпятся разобраться надокукцк
    @Test
    public void getAllUsersSortDesc(){

        String sortType= "desc";
        Response sortedResponse = given().queryParam("sort", sortType)
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .extract().response();

        Response notSortedResponse = given()
                .get("https://fakestoreapi.com/users")
                .then().log().all().extract().response();


        List<Integer> sortedResponseIds = sortedResponse.jsonPath().getList("id");
        List<Integer> notSortedResponseIds = notSortedResponse.jsonPath().getList("id");


        //Можно конкретно сравнить полученные списки
        List<Integer> sortedByCode = notSortedResponseIds.stream()
                        .sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        Assertions.assertNotEquals(sortedResponseIds,notSortedResponseIds);
        Assertions.assertEquals(sortedByCode, sortedResponseIds);
    }



//заводка нового пользака с помощью аннотаций ломбока плагина pojo и сборка простыни json'a с помощью джексона
    @Test
    public void addedUserTest(){
        Name name = new Name("Lexa","Lexus");
        Geolocation geolocation = new Geolocation("-37.3159","81.1496");
        Address address = Address.builder()
                .city("Moscow")
                .number(99)
                .zipcode("73500-1505")
                .street("Prospekt Mira")
                .geolocation(geolocation).build();


        UserRoot bodyRequest = UserRoot.builder()
                .name(name)
                .phone("8985997340")
                .email("fakemail@gmail.com")
                .username("ysicy")
                .password("ololololo")
                .address(address).build();

        given().body(bodyRequest)
                .post("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200)
                .body("id", notNullValue());

    }


    private UserRoot getTestuser() {

        Name name = new Name("Lexa", "Lexus");
        Geolocation geolocation = new Geolocation("-37.3159", "81.1496");
        Address address = Address.builder()
                .city("Moscow")
                .number(99)
                .zipcode("73500-1505")
                .street("Prospekt Mira")
                .geolocation(geolocation).build();


        return UserRoot.builder()
                .name(name)
                .phone("8985997340")
                .email("fakemail@gmail.com")
                .username("ysicy")
                .password("ololololo")
                .address(address).build();

    }

    @Test
    public void updateUsertest(){
        UserRoot user = getTestuser();
        String oldPassword = user.getPassword();

        user.setPassword("testingapi");
        given().body(user)
                .put("https://fakestoreapi.com/users/7")
                .then().log().all();

    }
@Test
    public void deleteUserTest(){
        given().delete("https://fakestoreapi.com/users/7")
                .then().log().all()
                .statusCode(200);
}

@Test
    public void sendAuthTest(){
    Map<String, String> userAuth = new HashMap<>();
    userAuth.put("username", "jimmie_k");
    userAuth.put("password", "klein*#%*");

        given().contentType(ContentType.JSON).body(userAuth)
                .post("https://fakestoreapi.com/auth/login")
                .then().log().all()
                .statusCode(200)
                .body("token",notNullValue());
}








}


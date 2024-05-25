package com.elena.test;

import com.elena.json.User;
import com.elena.json.UserLoginCreds;
import com.elena.teststeps.RequestEndpoints;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;

import static com.elena.utils.URLs.*;
import static com.elena.utils.Messages.*;
import static org.hamcrest.Matchers.*;

import io.qameta.allure.Description;
import com.github.javafaker.Faker;

public class UpdateUserTest {
    RequestEndpoints requestEndpoints = new RequestEndpoints();
    static Faker faker = new Faker();
    static String email = faker.internet().emailAddress();
    static String password = faker.internet().password();
    static String name = faker.name().firstName();
    static String emptyString = "";

    static User newUser = new User(email, password, name);
    static UserLoginCreds newUserLogin = new UserLoginCreds(email, password);

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Description("Check user data successfully updated")
    public void checkUserDataUpdated(){
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);

        checkStatusCode(responseRegister, 200);

        System.out.println(responseRegister.asString());
        System.out.println(newUser.getEmail());
        System.out.println(newUser.getName());
        System.out.println(newUser.getPassword());
        String accessToken = extractAccessToken(responseRegister);
        System.out.println(extractAccessToken(responseRegister));

        Response updateDataResp = requestEndpoints.updateUserData(accessToken);
        //добавить данные для обновления имеил и имени пользователя
        //добавить данные для обновления имеил пользователя
        //добавить данные для обновления имени пользователя

    }

    //============================================================================
    @Step("Check api/auth/register response code")  //регистрация и логин
    public void checkStatusCode(Response response, int expectedCode){
        response.then().assertThat().statusCode(expectedCode);
    }
    @Step("Check api/auth/register response") // //регистрация и логин
    public void checkResponseMessage(Response response, String message){
        response.then().assertThat().body("message", equalTo(message));
    }
    @Step("Check status in response body")
    public void checkResponseBody(Response response, String key, boolean expectedBody){
        response.then().assertThat().body(key, equalTo(expectedBody));
    }
    @Step("CHeck access token exists") //регистрация
    public void checkAccessToken(Response response){
        response.then().assertThat().body("accessToken", notNullValue());
    }
    @Step("Check user details") //регистрация и логин
    public void checkUserDetails(Response response, String expectedEmail, String expectedName){
        response.then().assertThat().body("user.email", equalTo(expectedEmail));
        response.then().assertThat().body("user.name", equalTo(expectedName));
    }
    @Step("Extract access token") //регистрация и логин
    public String extractAccessToken(Response response){
        String accessToken = response.then().extract().path("accessToken");
        return accessToken;
    }

    @Step("Extrace refresh token") //регистрация и логин
    public String extractRefreshToken(Response response){
        String refreshToken = response.then().extract().path("refreshToken");
        return refreshToken;
    }
}

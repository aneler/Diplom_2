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

public class UserRegistrationTest {
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
    @Description("Check user registration and login")
    public void userRegistration(){
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);

        checkStatusCode(responseRegister, 200);

        System.out.println(responseRegister.asString());

        checkResponseBody(responseRegister, "success", true);
        checkAccessToken(responseRegister);
        checkUserDetails(responseRegister, newUser.getEmail(), newUser.getName());
        String accessToken = extractAccessToken(responseRegister);

        Response responseLogin = requestEndpoints.sendLoginRequest(newUserLogin);
        checkStatusCode(responseLogin, 200);
        //подумать над проверкой имени
        //сделать проверку ответа пришедшего после авторизации
        checkUserDetails(responseLogin, newUserLogin.getEmail(), newUser.getName());
        System.out.println("=========="+extractRefreshToken(responseLogin));

        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        checkStatusCode(responseDelete, 202);
    }

    @Test
    @Description
    public void checkRegistrationExistingEmail(){
        Response firstResponse = requestEndpoints.sendRegisterRequest(newUser);
        checkStatusCode(firstResponse, 200);

        Response secondResponse = requestEndpoints.sendRegisterRequest(newUser);
        checkStatusCode(secondResponse, 403);
        checkResponseMessage(secondResponse, USER_EXISTS);
        checkResponseBody(secondResponse, "success", false);
    }
    @Test
    @Description
    public void checkRegistrationMissingEmail(){
        User missingEmail = new User(emptyString, password, name);
        Response response = requestEndpoints.sendRegisterRequest(missingEmail);
        checkStatusCode(response, 403);
        checkResponseMessage(response, MISSING_FIELD_MSG);
        checkResponseBody(response, "success", false);
    }
    @Test
    @Description
    public void checkRegistrationMissingPassword(){
        User missingPassword = new User(email, emptyString, name);
        Response response = requestEndpoints.sendRegisterRequest(missingPassword);
        checkStatusCode(response, 403);
        checkResponseMessage(response, MISSING_FIELD_MSG);
        checkResponseBody(response, "success", false);
    }

    @Test
    @Description
    public void checkRegistrationMissingName(){
        User missingName = new User(email, password, emptyString);
        Response response = requestEndpoints.sendRegisterRequest(missingName);
        checkStatusCode(response, 403);
        checkResponseMessage(response, MISSING_FIELD_MSG);
        checkResponseBody(response, "success", false);
    }
    //=============LOGIN TESTS===============================================
    @Test
    @Description("Incorrect password")
    public void checkIncorrectPassword(){
        User user = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response response = requestEndpoints.sendRegisterRequest(user);
        checkStatusCode(response, 200);
        UserLoginCreds incorrectPassword = new UserLoginCreds(user.getEmail(), "123123");
        Response responseLogin = requestEndpoints.sendLoginRequest(incorrectPassword);
        checkResponseMessage(responseLogin, INCORRECT_EMAIL_PASSWORD);
    }

    @Test
    @Description("Incorrect login")
    public void checkIncorrectLogin(){
        //Response response = requestEndpoints.sendRegisterRequest(newUser);
        UserLoginCreds incorrectLogin = new UserLoginCreds("test@test.ru", "123123");
        Response responseLogin = requestEndpoints.sendLoginRequest(incorrectLogin);
        checkResponseMessage(responseLogin, INCORRECT_EMAIL_PASSWORD);
    }

    //=======================================================================
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

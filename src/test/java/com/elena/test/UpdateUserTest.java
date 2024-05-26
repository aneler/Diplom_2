package com.elena.test;

import com.elena.json.User;
import com.elena.json.UserData;
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

    /*static User newUser = new User(email, password, name);
    static UserLoginCreds newUserLogin = new UserLoginCreds(email, password);*/

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Description("Check user email and data are successfully updated")
    public void checkUserEmailAndNameUpdated(){
        //new user registration
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        checkStatusCode(InitialResponse, 200);
        //get access token
        String accessToken = extractAccessToken(InitialResponse);

        //update user data using token
        UserData updatedUserData = new UserData(faker.internet().emailAddress(), faker.name().firstName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        checkStatusCode(updatedResponse, 200);

        //check user data successfully updated
        checkUserDetails(updatedResponse, updatedUserData.getEmail(), updatedUserData.getName());
        //delete just created user
        requestEndpoints.sendDeleteUserRequest(accessToken);

        //добавить данные для обновления имеил и имени пользователя
        //добавить данные для обновления имеил пользователя
        //добавить данные для обновления имени пользователя

    }
    @Test
    @Description("Check user email is successfully updated")
    public void checkUserEmailUpdated(){
        //new user registration
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        checkStatusCode(InitialResponse, 200);
        //get access token
        String accessToken = extractAccessToken(InitialResponse);

        //update user email
        UserData updatedUserData = new UserData(faker.internet().emailAddress(), initialUserData.getName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        checkStatusCode(updatedResponse, 200);

        //check user email successfully updated
        checkUserDetails(updatedResponse, updatedUserData.getEmail(), updatedUserData.getName());
        requestEndpoints.sendDeleteUserRequest(accessToken);

        //добавить данные для обновления имеил пользователя
        //добавить данные для обновления имени пользователя
    }

    @Test
    @Description("Check user name is successfully updated")
    public void checkUserNameUpdated(){
        //new user registration
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        checkStatusCode(InitialResponse, 200);
        //get access token
        String accessToken = extractAccessToken(InitialResponse);

        //update user name
        UserData updatedUserData = new UserData(initialUserData.getEmail(), faker.name().firstName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        checkStatusCode(updatedResponse, 200);

        //check user email successfully updated
        checkUserDetails(updatedResponse, updatedUserData.getEmail(), updatedUserData.getName());
        requestEndpoints.sendDeleteUserRequest(accessToken);

        //добавить данные для обновления имени пользователя
    }
    @Test
    @Description("Update user data without auth")
    public void checkUpdateUserWithoutToken(){
        UserData newUser = new UserData(faker.internet().emailAddress(), faker.name().firstName());
        Response responseNoToken = requestEndpoints.updateUserData("", newUser);
        checkStatusCode(responseNoToken, 401);
        checkResponseMessage(responseNoToken, TOKEN_MISSING);
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

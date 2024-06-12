package com.elena.test;

import com.elena.json.User;
import com.elena.json.UserLoginCreds;
import com.elena.teststeps.CommonTestSteps;
import com.elena.teststeps.TokenSteps;
import com.elena.teststeps.RequestEndpoints;
import com.elena.teststeps.UserDetailsSteps;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static com.elena.utils.URLs.*;
import static com.elena.utils.Messages.*;

import io.qameta.allure.Description;
import com.github.javafaker.Faker;

public class UserRegistrationTest {
    CommonTestSteps commonTestSteps = new CommonTestSteps();
    TokenSteps tokenSteps = new TokenSteps();
    UserDetailsSteps userDetailsSteps = new UserDetailsSteps();
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
        commonTestSteps.checkStatusCode(responseRegister, 200);

        commonTestSteps.checkResponseBody(responseRegister, "success", true);
        tokenSteps.checkAccessToken(responseRegister);
        userDetailsSteps.checkUserEmail(responseRegister, "user.email", newUser.getEmail());
        userDetailsSteps.checkUserName(responseRegister, "user.name", newUser.getName());
        String accessToken = tokenSteps.extractAccessToken(responseRegister);

        Response responseLogin = requestEndpoints.sendLoginRequest(newUserLogin);
        commonTestSteps.checkStatusCode(responseLogin, 200);
        //сделать проверку ответа пришедшего после авторизации
        userDetailsSteps.checkUserEmail(responseLogin, "user.email", newUser.getEmail());
        userDetailsSteps.checkUserName(responseLogin, "user.name", newUser.getName());

        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        commonTestSteps.checkStatusCode(responseDelete, 202);
    }

    @Test
    @Description("Check user registration with existing email")
    public void checkRegistrationExistingEmail(){
        //зарегистрировать пользователя
        Response firstResponse = requestEndpoints.sendRegisterRequest(newUser);
        commonTestSteps.checkStatusCode(firstResponse, 200);
        //зарегистрировать пользователя с теми же регистрационными данными
        Response secondResponse = requestEndpoints.sendRegisterRequest(newUser);
        commonTestSteps.checkStatusCode(secondResponse, 403);
        commonTestSteps.checkResponseMessage(secondResponse, USER_EXISTS);
        commonTestSteps.checkResponseBody(secondResponse, "success", false);
    }
    @Test
    @Description("Check user registration with missing email")
    public void checkRegistrationMissingEmail(){
        User missingEmail = new User(emptyString, password, name);
        Response response = requestEndpoints.sendRegisterRequest(missingEmail);
        commonTestSteps.checkStatusCode(response, 403);
        commonTestSteps.checkResponseMessage(response, MISSING_FIELD_MSG);
        commonTestSteps.checkResponseBody(response, "success", false);
    }
    @Test
    @Description("Check user registration with missing password")
    public void checkRegistrationMissingPassword(){
        User missingPassword = new User(email, emptyString, name);
        Response response = requestEndpoints.sendRegisterRequest(missingPassword);
        commonTestSteps.checkStatusCode(response, 403);
        commonTestSteps.checkResponseMessage(response, MISSING_FIELD_MSG);
        commonTestSteps.checkResponseBody(response, "success", false);
    }

    @Test
    @Description("Check user registration with missing name")
    public void checkRegistrationMissingName(){
        User missingName = new User(email, password, emptyString);
        Response response = requestEndpoints.sendRegisterRequest(missingName);
        commonTestSteps.checkStatusCode(response, 403);
        commonTestSteps.checkResponseMessage(response, MISSING_FIELD_MSG);
        commonTestSteps.checkResponseBody(response, "success", false);
    }
    //=============LOGIN TESTS===============================================
    @Test
    @Description("Check login with incorrect password")
    public void checkIncorrectPassword(){
        User user = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response response = requestEndpoints.sendRegisterRequest(user);
        commonTestSteps.checkStatusCode(response, 200);
        UserLoginCreds incorrectPassword = new UserLoginCreds(user.getEmail(), "123123");
        Response responseLogin = requestEndpoints.sendLoginRequest(incorrectPassword);
        commonTestSteps.checkResponseMessage(responseLogin, INCORRECT_EMAIL_PASSWORD);
    }

    @Test
    @Description("Check login with incorrect login")
    public void checkIncorrectLogin(){
        UserLoginCreds incorrectLogin = new UserLoginCreds("test@test.ru", "123123");
        Response responseLogin = requestEndpoints.sendLoginRequest(incorrectLogin);
        commonTestSteps.checkResponseMessage(responseLogin, INCORRECT_EMAIL_PASSWORD);
    }
}

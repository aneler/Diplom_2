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

public class UserLoginTest {
    RequestEndpoints requestEndpoints = new RequestEndpoints();
    static Faker faker = new Faker();
    static String email = faker.internet().emailAddress();
    static String password = faker.internet().password();
    static String name = faker.name().firstName();
    static String emptyString = "";

    static User newUser = new User(email, password, name);
    static UserLoginCreds newUserLogin = new UserLoginCreds(email, password);

    @Test
    @Description("Incorrect login")
    public void checkIncorrectLogin(){
        Response response = requestEndpoints.sendRegisterRequest(newUser);

    }
}

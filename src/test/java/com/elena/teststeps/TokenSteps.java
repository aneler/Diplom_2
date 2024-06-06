package com.elena.teststeps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;

public class TokenSteps {
    @Step("Extract access token") //регистрация и логин
    public String extractAccessToken(Response response){
        String accessToken = response.then().extract().path("accessToken");
        return accessToken;
    }

    @Step("Extract refresh token") //регистрация и логин
    public String extractRefreshToken(Response response){
        String refreshToken = response.then().extract().path("refreshToken");
        return refreshToken;
    }
    @Step("Check access token exists") //регистрация
    public void checkAccessToken(Response response){
        response.then().assertThat().body("accessToken", notNullValue());
    }
}

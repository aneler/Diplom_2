package com.elena.teststeps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CommonTestSteps {

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
    @Step("Check response data")
    public void checkResponseValueNotNull(Response response, String key){
        response.then().assertThat().body(key, notNullValue());
    }
}

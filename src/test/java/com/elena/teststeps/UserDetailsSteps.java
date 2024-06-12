package com.elena.teststeps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;

public class UserDetailsSteps {
    @Step("Check user details")
    public void checkUserEmail(Response response, String key, String expectedEmail){
        response.then().assertThat().body(key, equalTo(expectedEmail));
    }
    @Step("Check user details")
    public void checkUserName(Response response, String key, String expectedName){
        response.then().assertThat().body(key, equalTo(expectedName));
    }
}

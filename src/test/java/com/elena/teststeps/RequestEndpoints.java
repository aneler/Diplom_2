package com.elena.teststeps;

import com.elena.json.User;
import com.elena.json.UserData;
import com.elena.json.UserLoginCreds;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static com.elena.utils.URLs.*;
import static io.restassured.RestAssured.given;

public class RequestEndpoints {
    @Step("Send POST to api/auth/register")
    public Response sendRegisterRequest(User user){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(REGISTER_USER);
        return response;
    }
    @Step("Send DELETE user to api/auth/user")
    public Response sendDeleteUserRequest(String token){
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete(DELETE_USER);
        return response;
    }
    @Step("Send POST to api/auth/login")
    public Response sendLoginRequest(UserLoginCreds userLoginCreds){
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(userLoginCreds)
                .when()
                .post(LOGIN_USER);
        return response;
    }
    @Step("Send GET to api/auth/user")
    public Response getUserData(String token){
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(GET_USER_DATA);
        return response;
    }
    @Step("Send PATCH to api/auth/user")
    public Response updateUserData(String token, UserData user){
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(UPDATE_USER_DATA);
        return response;
    }
}

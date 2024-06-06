package com.elena.test;

import com.elena.json.User;
import com.elena.json.UserData;
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
import static org.hamcrest.Matchers.*;

import io.qameta.allure.Description;
import com.github.javafaker.Faker;

public class UpdateUserTest {
    CommonTestSteps commonTestSteps = new CommonTestSteps();
    TokenSteps tokenSteps = new TokenSteps();
    UserDetailsSteps userDetailsSteps = new UserDetailsSteps();
    RequestEndpoints requestEndpoints = new RequestEndpoints();
    static Faker faker = new Faker();
    /*static String email = faker.internet().emailAddress();
    static String password = faker.internet().password();
    static String name = faker.name().firstName();
    static String emptyString = "";*/


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Description("Check user email and data are successfully updated")
    public void checkUserEmailAndNameUpdated(){
        //регистрация нового пользователя
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        commonTestSteps.checkStatusCode(InitialResponse, 200);
        //получить токен авторизации
        String accessToken = tokenSteps.extractAccessToken(InitialResponse);

        //изменить данные пользователя
        UserData updatedUserData = new UserData(faker.internet().emailAddress(), faker.name().firstName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        commonTestSteps.checkStatusCode(updatedResponse, 200);

        //проверить, что данные пользователя успешно изменились
        userDetailsSteps.checkUserEmail(updatedResponse, "user.email", updatedUserData.getEmail());
        userDetailsSteps.checkUserName(updatedResponse, "user.name", updatedUserData.getName());
        //удаление только что созданного пользователя
        requestEndpoints.sendDeleteUserRequest(accessToken);
    }
    @Test
    @Description("Check user email is successfully updated")
    public void checkUserEmailUpdated(){
        //регистрация нового пользователя
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        commonTestSteps.checkStatusCode(InitialResponse, 200);
        //получить токен авторизации
        String accessToken = tokenSteps.extractAccessToken(InitialResponse);

        //изменить email
        UserData updatedUserData = new UserData(faker.internet().emailAddress(), initialUserData.getName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        commonTestSteps.checkStatusCode(updatedResponse, 200);

        //проверить, что email успешно сохранен
        userDetailsSteps.checkUserEmail(updatedResponse, "user.email", updatedUserData.getEmail());
        //удалить созданного пользвателя
        requestEndpoints.sendDeleteUserRequest(accessToken);
    }

    @Test
    @Description("Check user name is successfully updated")
    public void checkUserNameUpdated(){
        //регистрация нового пользователя
        User initialUserData = new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName());
        Response InitialResponse = requestEndpoints.sendRegisterRequest(initialUserData);
        commonTestSteps.checkStatusCode(InitialResponse, 200);
        //получить токен авторизации
        String accessToken = tokenSteps.extractAccessToken(InitialResponse);

        //изменить имя пользователя
        UserData updatedUserData = new UserData(initialUserData.getEmail(), faker.name().firstName());
        Response updatedResponse = requestEndpoints.updateUserData(accessToken, updatedUserData);
        commonTestSteps.checkStatusCode(updatedResponse, 200);

        //проверить, что имя пользователя успешно сохранилось
        userDetailsSteps.checkUserName(updatedResponse, "user.name", updatedUserData.getName());
        //удалить созданного пользователя
        requestEndpoints.sendDeleteUserRequest(accessToken);
    }
    @Test
    @Description("Update user data without authentication")
    public void checkUpdateUserWithoutToken(){
        //создать пользователя
        UserData newUser = new UserData(faker.internet().emailAddress(), faker.name().firstName());
        //попытка авторизации с недействительным токеном авторизации
        Response responseIncorrectToken = requestEndpoints.updateUserData("", newUser);
        commonTestSteps.checkStatusCode(responseIncorrectToken, 401);
        commonTestSteps.checkResponseMessage(responseIncorrectToken, TOKEN_MISSING);
    }
}

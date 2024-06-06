package com.elena.test;

import com.elena.json.*;
import com.elena.teststeps.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static com.elena.utils.URLs.*;
import static com.elena.utils.Messages.*;
import io.qameta.allure.Description;
import com.github.javafaker.Faker;

import java.util.*;

public class GetOrdersTest {
    CommonTestSteps commonTestSteps = new CommonTestSteps();
    TokenSteps tokenSteps = new TokenSteps();
    OrderSteps orderSteps = new OrderSteps();
    IngredientSteps ingredientSteps = new IngredientSteps();
    UserDetailsSteps userDetailsSteps = new UserDetailsSteps();
    RequestEndpoints requestEndpoints = new RequestEndpoints();
    Faker faker = new Faker();
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }
    @Test
    @Description
    public void getOrderListNonAuthenticatedUser(){
        String accessToken = "";

        //запросить список заказов
        Response userOrders = requestEndpoints.getOrdersList(accessToken);
        commonTestSteps.checkResponseMessage(userOrders, TOKEN_MISSING);
        commonTestSteps.checkStatusCode(userOrders, 401);
        commonTestSteps.checkResponseBody(userOrders, "success", false);
    }
    @Test
    @Description
    public void getOrderListAuthenticatedUser(){
        //создать пользователя
        User newUser = new User(faker.internet().emailAddress(), "123qwe!Q", faker.name().firstName());
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);
        String accessToken = tokenSteps.extractAccessToken(responseRegister);
        //сделать заказ, получить список заказов, в списке не должно быть заказов
        Response  userOrdersBefore = requestEndpoints.getOrdersList(accessToken);
        orderSteps.checkAmountOfOrders(userOrdersBefore, 0);
        //получение списка доступных ингредиентов
        Response response = requestEndpoints.getIngredientList();
        commonTestSteps.checkStatusCode(response, 200);

        //выбрать из списка ингредиентов 3 случайных ингредиента и добавить в заказ
        String bunId = ingredientSteps.getRandomIngredient(response, "bun");
        String sauceId = ingredientSteps.getRandomIngredient(response, "sauce");
        String mainId = ingredientSteps.getRandomIngredient(response, "main");
        IngredientsRequest ingredientsList = new IngredientsRequest(Arrays.asList(bunId, sauceId, mainId));
        Response orderResponse = requestEndpoints.createOrder(accessToken, ingredientsList);
        commonTestSteps.checkStatusCode(orderResponse, 200);
        //запросить список заказов
        Response userOrdersAfter = requestEndpoints.getOrdersList(accessToken);
        //проверить, что в списке ожидаемое количество заказов
        orderSteps.checkAmountOfOrders(userOrdersAfter, 1);
        //получить номер сделанного заказа
        String orderNumber = orderSteps.extractOrderNumber(orderResponse);
        //проверить, что список содержит только что сделанный заказ
        orderSteps.checkOrdersListContainOrder(userOrdersAfter, orderNumber);
        //удалить пользователя
        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        commonTestSteps.checkStatusCode(responseDelete, 202);
    }

}

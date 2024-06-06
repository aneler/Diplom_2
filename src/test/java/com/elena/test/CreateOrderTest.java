package com.elena.test;

import com.elena.json.*;
import com.elena.teststeps.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.elena.utils.URLs.*;
import static com.elena.utils.Messages.*;
import static org.hamcrest.Matchers.*;

import io.qameta.allure.Description;
import com.github.javafaker.Faker;

import java.util.*;

public class CreateOrderTest {
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
    @Description("Authenticated user makes an order")
    public void makeOrderForAuthenticatedUser(){
        //сделать нового пользователя
        User newUser = new User(faker.internet().emailAddress(), "123qwe!Q", faker.name().firstName());
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);
        String accessToken = tokenSteps.extractAccessToken(responseRegister);

        //получение списка доступных ингредиентов
        Response response = requestEndpoints.getIngredientList();
        commonTestSteps.checkStatusCode(response, 200);

        //выбрать из списка ингредиентов 3 случайных ингредиента
        String bunId = ingredientSteps.getRandomIngredient(response, "bun");
        String sauceId = ingredientSteps.getRandomIngredient(response, "sauce");
        String mainId = ingredientSteps.getRandomIngredient(response, "main");
        IngredientsRequest ingredientsList = new IngredientsRequest(Arrays.asList(bunId, sauceId, mainId));
        //авторизоваться
        //сформировать запрос для заказа и отправить
        Response orderResponse = requestEndpoints.createOrder(accessToken, ingredientsList);
        commonTestSteps.checkStatusCode(orderResponse, 200);
        //проверить, что ответ содержит success true
        commonTestSteps.checkResponseBody(orderResponse, "success", true);
        //секция order содержит cтолько элементов, сколько передавалось в ingredientsList при создании заказа
        ingredientSteps.checkIngredientAmount(orderResponse, ingredientsList.getAmountOfIngredients());
        //здесь проверить данные пользователя имя и email
        userDetailsSteps.checkUserEmail(orderResponse, "order.owner.email", newUser.getEmail());
        userDetailsSteps.checkUserName(orderResponse, "order.owner.name", newUser.getName());
        // и каждый ингредиент содержит нужный id из списка
        ingredientSteps.checkIngredientPresent(orderResponse, bunId, "bun");
        ingredientSteps.checkIngredientPresent(orderResponse, sauceId, "sauce");
        ingredientSteps.checkIngredientPresent(orderResponse, mainId, "main");
        //не пустой номер заказа
        commonTestSteps.checkResponseValueNotNull(orderResponse, "order.number");
        //не пустая цена заказа
        commonTestSteps.checkResponseValueNotNull(orderResponse, "order.price");

        //удалить пользователя
        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        commonTestSteps.checkStatusCode(responseDelete, 202);
    }

    @Test
    @Description("Order has empty ingredients list")
    public void makeOrderWithEmptyIngredientsList(){
        //cделать нового пользователя и запомнить sccessToken
        User newUser = new User(faker.internet().emailAddress(), "123qwe!Q", faker.name().firstName());
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);
        String accessToken = tokenSteps.extractAccessToken(responseRegister);
        //отправить заказ с пустым списком ингредиентов
        IngredientsRequest ingredientsList = new IngredientsRequest(Collections.emptyList());
        Response orderResponse = requestEndpoints.createOrder(accessToken, ingredientsList);
        //проверить ответ
        commonTestSteps.checkStatusCode(orderResponse, 400);
        //удалить пользователя
        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        commonTestSteps.checkStatusCode(responseDelete, 202);
    }

    //тест падает. в документации описано, что ожидаемый ответа при передаче невалидного id ингредиента
    //ожидается статус код 500, фактически возвращается 400
    @Test
    @Description("Order has incorrect ingredient hash")
    public void makeOrderWithIncorrectIngredientHash(){
        //cделать нового пользователя и запомнить accessToken
        User newUser = new User(faker.internet().emailAddress(), "123qwe!Q", faker.name().firstName());
        Response responseRegister = requestEndpoints.sendRegisterRequest(newUser);
        String accessToken = tokenSteps.extractAccessToken(responseRegister);
        //отправить заказ с некорректным хешем ингредиента
        IngredientsRequest ingredientsList = new IngredientsRequest(Arrays.asList("111111111111"));
        Response orderResponse = requestEndpoints.createOrder(accessToken, ingredientsList);
        //проверить ответ
        commonTestSteps.checkStatusCode(orderResponse, 500);
        commonTestSteps.checkResponseMessage(orderResponse, INCORRECT_INGREDIENT_ID);
        commonTestSteps.checkResponseBody(orderResponse, "sucess", false);
        //удалить пользователя
        Response responseDelete = requestEndpoints.sendDeleteUserRequest(accessToken);
        commonTestSteps.checkStatusCode(responseDelete, 202);
    }

    @Test
    @Description("Non authenticated user makes an order")
    public void makeOrderForNonAuthenticatedUser(){
        //некорректный токен авторизации
        String accessToken = "";

        //получение списка доступных ингредиентов
        Response response = requestEndpoints.getIngredientList();
        commonTestSteps.checkStatusCode(response, 200);

        //выбрать из списка ингредиентов 3 случайных ингредиента
        String bun = ingredientSteps.getRandomIngredient(response, "bun");
        String sauce = ingredientSteps.getRandomIngredient(response, "sauce");
        String main = ingredientSteps.getRandomIngredient(response, "main");
        IngredientsRequest ingredientsList = new IngredientsRequest(Arrays.asList(bun, sauce, main));
        //сформировать запрос для заказа с некорректным токетом авторизации и отпарвить
        Response orderResponse = requestEndpoints.createOrder(accessToken, ingredientsList);
        commonTestSteps.checkStatusCode(orderResponse, 200);
        commonTestSteps.checkResponseBody(orderResponse, "success", true);
        //проверить, что есть бургер и его название не пустое
        commonTestSteps.checkResponseValueNotNull(orderResponse, "name");
        commonTestSteps.checkResponseValueNotNull(orderResponse, "order.number");
    }

}

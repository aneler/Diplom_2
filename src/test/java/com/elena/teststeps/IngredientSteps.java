package com.elena.teststeps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;

public class IngredientSteps {
    @Step("GET random ingredient by type")
    public String getRandomIngredient(Response response, String ingredientType){
        List<Map<String, Object>> ingredients = response.jsonPath().getList("data");

        List<String> ingredientIds = ingredients.stream()
                .filter(ingredient -> ingredientType.equals(ingredient.get("type")))
                .map(ingredient -> (String) ingredient.get("_id"))
                .collect(Collectors.toList());
        Random random = new Random();
        return ingredientIds.get(random.nextInt(ingredientIds.size()));
    }
    @Step("Find an ingredient by id in order and check its type")
    public void checkIngredientPresent(Response response, String ingredientId, String type){
        response.then().assertThat().body("order.ingredients.find { it._id == '" + ingredientId + "' }.type", equalTo(type));
    }
    @Step("Check amount of ingredients in order")
    public void checkIngredientAmount(Response response, int ingredientAmount){
        response.then().assertThat().body("order.ingredients.size()", equalTo(ingredientAmount));
    }
}

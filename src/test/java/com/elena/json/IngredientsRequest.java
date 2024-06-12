package com.elena.json;

import java.util.List;

public class IngredientsRequest {
    private List<String> ingredients;

    public IngredientsRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public int getAmountOfIngredients(){
        return this.ingredients.size();
    }
}


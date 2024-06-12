package com.elena.teststeps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class OrderSteps {
    @Step("Check amount of orders")
    public void checkAmountOfOrders(Response response, int expectedAmountOfOrders){
        response.then().assertThat().body("orders.size()", equalTo(expectedAmountOfOrders));
    }
    @Step("Extract order number from create order response")
    public String extractOrderNumber(Response response){
        String orderNumber = response.jsonPath().getString("order.number");
        return orderNumber;
    }
    @Step("Find order number in orders list")
    public void checkOrdersListContainOrder(Response response, String expectedOrderNumber){
        List<String> allOrderNumbers = response.jsonPath().getList("orders.number");
        Integer expectedOrderNumberInt = Integer.parseInt(expectedOrderNumber);
        Assert.assertTrue(expectedOrderNumber, allOrderNumbers.contains(expectedOrderNumberInt));
    }
}

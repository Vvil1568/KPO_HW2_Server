package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class AddDishToOrderEndpoint extends BasicEndpoint {

    public AddDishToOrderEndpoint() {
        super("/order/adddish");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"dish_id"})
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        int dishId = body.get("dish_id").getAsInt();

        if(!DatabaseManager.getInstance().orderManager.addDishToOrder(token, dishId)){
            sendResponse(http, 400, "An error occurred during the addition of dish to order");
            return;
        }
        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
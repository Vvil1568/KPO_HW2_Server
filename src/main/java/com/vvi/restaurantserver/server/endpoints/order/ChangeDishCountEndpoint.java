package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class ChangeDishCountEndpoint extends BasicEndpoint {

    public ChangeDishCountEndpoint() {
        super("/order/changedishcount");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"dish_id", "delta"})
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        int dishId = body.get("dish_id").getAsInt();
        int delta = body.get("delta").getAsInt();
        int newCount = DatabaseManager.getInstance().orderManager.changeDishCount(token, dishId, delta);
        if (newCount == -1) {
            sendResponse(http, 400, "An error occurred during the change of dish count in order");
            return;
        }
        JsonObject response = new JsonObject();
        response.add("new_count", new JsonPrimitive(newCount));
        sendResponse(http, 200, response.toString());
    }
}
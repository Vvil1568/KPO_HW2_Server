package com.vvi.restaurantserver.server.endpoints.dish;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

public class AddDishEndpoint extends BasicEndpoint {

    public AddDishEndpoint() {
        super("/dish/add");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"name", "desc", "price", "time"})
                .setRequireToken()
                .setAdminOnly();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        String name = body.get("name").getAsString();
        String desc = body.get("desc").getAsString();
        double price = body.get("price").getAsDouble();
        long time = body.get("time").getAsLong();
        Dish newDish = DatabaseManager.getInstance().dishManager.addDish(new Dish(name, desc, price, time));
        if(newDish.getId()==-1){
            sendResponse(http, 400, "An error occurred during the dish addition");
            return;
        }
        JsonObject response = JsonSerialisationUtils.toJson(newDish);
        sendResponse(http, 200, response.toString());
    }
}
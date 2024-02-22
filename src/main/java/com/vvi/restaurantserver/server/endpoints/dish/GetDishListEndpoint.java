package com.vvi.restaurantserver.server.endpoints.dish;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.ArrayList;

public class GetDishListEndpoint extends BasicEndpoint {

    public GetDishListEndpoint() {
        super("/dish/get");
        new Builder(this)
                .setRequestMethod(RequestMethod.GET)
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        ArrayList<Dish> dishes = DatabaseManager.getInstance().dishManager.getDishList();
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (Dish dish : dishes) {
            array.add(JsonSerialisationUtils.toJson(dish));
        }
        response.add("dishes", array);
        sendResponse(http, 200, response.toString());
    }
}

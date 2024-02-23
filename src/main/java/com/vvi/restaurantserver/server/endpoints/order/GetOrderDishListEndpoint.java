package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.AbstractMap;
import java.util.ArrayList;

public class GetOrderDishListEndpoint extends BasicEndpoint {

    public GetOrderDishListEndpoint() {
        super("/order/dishlist");
        new Builder(this)
                .setRequestMethod(RequestMethod.GET)
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        ArrayList<AbstractMap.SimpleEntry<Dish, Integer>> dishes = DatabaseManager.getInstance().orderManager.getOrderDishList(token);
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (AbstractMap.SimpleEntry<Dish, Integer> dish : dishes) {
            JsonObject dishObj = new JsonObject();
            dishObj.add("dish", JsonSerialisationUtils.toJson(dish.getKey()));
            dishObj.add("count", new JsonPrimitive(dish.getValue()));
            array.add(dishObj);
        }
        response.add("dishes", array);
        sendResponse(http, 200, response.toString());
    }
}

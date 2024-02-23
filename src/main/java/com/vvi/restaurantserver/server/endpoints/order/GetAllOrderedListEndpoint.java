package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.ArrayList;

public class GetAllOrderedListEndpoint extends BasicEndpoint {

    public GetAllOrderedListEndpoint() {
        super("/order/alldishlist");
        new Builder(this)
                .setRequestMethod(RequestMethod.GET)
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        ArrayList<Dish> dishes = DatabaseManager.getInstance().orderManager.getAllOrderedList(token);
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (Dish dish : dishes) {
            array.add(JsonSerialisationUtils.toJson(dish));
        }
        response.add("dishes", array);
        sendResponse(http, 200, response.toString());
    }
}

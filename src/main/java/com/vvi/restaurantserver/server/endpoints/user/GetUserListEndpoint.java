package com.vvi.restaurantserver.server.endpoints.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.database.items.User;
import com.vvi.restaurantserver.database.tables.UserManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.AbstractMap;
import java.util.ArrayList;

public class GetUserListEndpoint extends BasicEndpoint {
    private final UserManager userManager;

    public GetUserListEndpoint() {
        super("/user/getList");
        new Builder(this)
                .setRequestMethod(RequestMethod.GET)
                .setRequireToken()
                .setAdminOnly();
        this.userManager = DatabaseManager.getInstance().userManager;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        ArrayList<User> users = userManager.getUserList(token);
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (User user : users) {
            array.add(JsonSerialisationUtils.toJson(user));
        }
        response.add("users", array);
        sendResponse(http, 200, response.toString());
    }
}
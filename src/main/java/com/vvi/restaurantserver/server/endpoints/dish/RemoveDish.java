package com.vvi.restaurantserver.server.endpoints.dish;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class RemoveDish extends BasicEndpoint {

    public RemoveDish() {
        super("/dish/remove");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"id"})
                .setRequireToken()
                .setAdminOnly();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        int id = body.get("id").getAsInt();
        if(!DatabaseManager.getInstance().dishManager.deleteDish(id)){
            sendResponse(http, 400, "An error occurred during the dish removal");
            return;
        }
        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
package com.vvi.restaurantserver.server.endpoints.statistics;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class GetFavouriteDishEndpoint extends BasicEndpoint {

    public GetFavouriteDishEndpoint() {
        super("/stats/favouritedish");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"time_from", "time_to"})
                .setRequireToken()
                .setAdminOnly();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        long timeFrom = body.get("time_from").getAsLong();
        long timeTo = body.get("time_to").getAsLong();
        String favouriteDish = DatabaseManager.getInstance().statisticsManager.getFavouriteDish(timeFrom, timeTo);
        if (favouriteDish.isEmpty()) {
            sendResponse(http, 400, "An error occurred during the favourite dish evaluation");
            return;
        }
        JsonObject response = new JsonObject();
        response.add("favourite_dish", new JsonPrimitive(favouriteDish));
        sendResponse(http, 200, response.toString());
    }
}
package com.vvi.restaurantserver.server.endpoints.statistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Comment;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.AbstractMap;
import java.util.ArrayList;

public class GetRatingsEndpoint extends BasicEndpoint {

    public GetRatingsEndpoint() {
        super("/stats/ratings");
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
        ArrayList<AbstractMap.SimpleEntry<String, Double>> ratings =
                DatabaseManager.getInstance().statisticsManager.getRatings(timeFrom, timeTo);
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (AbstractMap.SimpleEntry<String, Double> rating : ratings) {
            JsonObject ratingObj = new JsonObject();
            ratingObj.add("name", new JsonPrimitive(rating.getKey()));
            ratingObj.add("rating", new JsonPrimitive(rating.getValue()));
            array.add(ratingObj);
        }
        response.add("ratings", array);
        sendResponse(http, 200, response.toString());
    }
}

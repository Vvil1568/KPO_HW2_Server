package com.vvi.restaurantserver.server.endpoints.statistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.Comment;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.utils.JsonSerialisationUtils;

import java.util.ArrayList;

public class GetCommentListEndpoint extends BasicEndpoint {

    public GetCommentListEndpoint() {
        super("/stats/commentlist");
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
        ArrayList<Comment> comments = DatabaseManager.getInstance().statisticsManager.getCommentList(timeFrom, timeTo);
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        for (Comment comment : comments) {
            array.add(JsonSerialisationUtils.toJson(comment));
        }
        response.add("comments", array);
        sendResponse(http, 200, response.toString());
    }
}

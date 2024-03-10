package com.vvi.restaurantserver.server.endpoints.comment;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class LeaveCommentEndpoint extends BasicEndpoint {

    public LeaveCommentEndpoint() {
        super("/comment/add");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"dish_id", "stars", "comment"})
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        int dish_id = body.get("dish_id").getAsInt();
        int stars = body.get("stars").getAsInt();
        String comment = body.get("comment").getAsString();
        if (!DatabaseManager.getInstance().commentManager.addComment(token, dish_id, comment, stars)) {
            sendResponse(http, 400, "An error occurred during the comment addition");
            return;
        }
        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
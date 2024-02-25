package com.vvi.restaurantserver.server.endpoints.statistics;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class GetTotalRevenueEndpoint extends BasicEndpoint {

    public GetTotalRevenueEndpoint() {
        super("/stats/totalrevenue");
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
        double totalRevenue = DatabaseManager.getInstance().statisticsManager.getTotalRevenue(timeFrom, timeTo);
        if(totalRevenue==-1){
            sendResponse(http, 400, "An error occurred during the total revenue count");
            return;
        }
        JsonObject response = new JsonObject();
        response.add("total_revenue", new JsonPrimitive(totalRevenue));
        sendResponse(http, 200, response.toString());
    }
}
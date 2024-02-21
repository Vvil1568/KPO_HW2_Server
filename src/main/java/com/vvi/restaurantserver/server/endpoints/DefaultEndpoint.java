package com.vvi.restaurantserver.server.endpoints;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class DefaultEndpoint extends BasicEndpoint {
    public DefaultEndpoint() {
        super("/");
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        sendResponse(http, 200, "Server is running");
    }
}

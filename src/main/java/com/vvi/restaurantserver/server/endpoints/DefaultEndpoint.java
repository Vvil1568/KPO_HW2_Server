package com.vvi.restaurantserver.server.endpoints;

import com.sun.net.httpserver.HttpExchange;

public class DefaultEndpoint extends BasicEndpoint {
    public DefaultEndpoint() {
        super("/");
    }

    @Override
    public void handle(HttpExchange http) {
        sendResponseJson(http, 200, "Server is running");
    }
}

package com.vvi.restaurantserver.server.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultEndpoint implements HttpHandler {
    @Override
    public void handle(HttpExchange http) throws IOException {
        String response = "Server is running";
        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, response.length());
        OutputStream os = http.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

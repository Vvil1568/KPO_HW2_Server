package com.vvi.restaurantserver.server.endpoints;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BasicEndpoint implements HttpHandler {
    protected String name;

    protected BasicEndpoint(String name) {
        this.name = name;
    }

    protected void sendResponseJson(HttpExchange http, int code, String string) {
        try {
            http.getResponseHeaders().add("Content-Type", "application/json");
            http.sendResponseHeaders(code, string.length());
            OutputStream os = http.getResponseBody();
            os.write(string.getBytes());
            os.close();
        } catch (IOException exception) {
            System.out.println(String.format("An error occured at endpoint \"%s\" while sending the response", name));
        }
    }
}
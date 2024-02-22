package com.vvi.restaurantserver.server.endpoints.base;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.tables.UserManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicEndpoint implements HttpHandler {
    public final String name;
    private RequestMethod requestMethod = RequestMethod.GET;
    protected static final Gson gson = new Gson();

    private String[] requiredFields = new String[]{};
    private boolean requireToken;
    protected String token = "";
    private boolean adminOnly;

    protected BasicEndpoint(String name) {
        this.name = name;
    }

    private JsonObject getBody(HttpExchange http) {
        Reader reader = new InputStreamReader(http.getRequestBody(), StandardCharsets.UTF_8);
        try {
            return gson.fromJson(reader, JsonObject.class);
        } catch (JsonIOException | JsonSyntaxException e) {
            logError("Cannot parse request body");
            return null;
        }
    }

    @Override
    public void handle(HttpExchange http) {
        if (!http.getRequestMethod().equals(requestMethod.name())) {
            sendResponse(http, 400, "Invalid request method!");
            return;
        }
        List<String> tokens = http.getRequestHeaders().get("Authorization");
        if(tokens!=null && !tokens.isEmpty() && tokens.get(0).startsWith("Bearer ")){
            this.token = tokens.get(0).substring(7);
        }
        if(requireToken && !DatabaseManager.getInstance().userManager.isValidUser(token)){
            sendResponse(http, 401, "Invalid authorisation token!");
            return;
        }
        if(adminOnly && !DatabaseManager.getInstance().userManager.isAdmin(token)){
            sendResponse(http, 401, "Invalid authorisation token!");
            return;
        }
        JsonObject body = getBody(http);
        if (!requireFields(http, body, requiredFields)) {
            return;
        }
        handle(http, body);
    }

    public abstract void handle(HttpExchange http, JsonObject body);

    protected void sendResponse(HttpExchange http, int code, String string) {
        try {
            http.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            string = new String(string.getBytes(), StandardCharsets.UTF_8);
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
            http.sendResponseHeaders(code, bytes.length);
            OutputStream os = http.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException exception) {
            logError("Can't send response");
        }
    }

    private boolean requireFields(HttpExchange http, JsonObject object, String[] fields) {
        ArrayList<String> missing = new ArrayList<>();
        for (String name : fields) {
            if (!object.has(name)) {
                missing.add(name);
            }
        }
        if (missing.isEmpty()) {
            return true;
        } else {
            sendResponse(http, 400, "Request body is incorrect: " + missing);
            return false;
        }
    }

    protected void logError(String message) {
        System.out.printf("An error occured at endpoint \"%s\":\n", name);
        System.out.println(message);
    }

    public static class Builder{
        BasicEndpoint endpoint;
        public Builder(BasicEndpoint endpoint){
            this.endpoint = endpoint;
        }

        public Builder setRequiredFields(String[] requiredFields){
            endpoint.requiredFields = requiredFields;
            return this;
        }

        public Builder setRequestMethod(RequestMethod method){
            endpoint.requestMethod = method;
            return this;
        }

        public Builder setRequireToken(){
            endpoint.requireToken = true;
            return this;
        }

        public Builder setAdminOnly(){
            endpoint.adminOnly = true;
            return this;
        }
    }
}
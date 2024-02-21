package com.vvi.restaurantserver.server.endpoints.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.items.User;
import com.vvi.restaurantserver.database.tables.UserManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

import java.util.AbstractMap;

public class RegistrationEndpoint extends BasicEndpoint {
    private final UserManager userManager;

    public RegistrationEndpoint(UserManager manager) {
        super("/registration");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"login", "passHash"});
        this.userManager = manager;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        String login = body.get("login").getAsString();
        String passHash = body.get("passHash").getAsString();
        String fio = body.has("fio") ? body.get("fio").getAsString() : "";
        if (userManager.loginExists(login)) {
            sendResponse(http, 400, "Login already exists");
            return;
        }
        if(!userManager.register(new User(fio, login, passHash, !userManager.hasUsers()))){
            sendResponse(http, 400, "An error occurred during the registration process");
            return;
        }
        JsonObject response = new JsonObject();
        AbstractMap.SimpleEntry<String, Boolean> result = userManager.login(login, passHash);
        response.add("token", new JsonPrimitive(result.getKey()));
        response.add("isAdmin", new JsonPrimitive(result.getValue()));
        sendResponse(http, 200, response.toString());
    }
}
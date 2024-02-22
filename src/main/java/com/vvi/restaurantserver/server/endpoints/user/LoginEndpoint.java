package com.vvi.restaurantserver.server.endpoints.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.tables.UserManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;

public class LoginEndpoint extends BasicEndpoint {
    private final UserManager userManager;

    public LoginEndpoint() {
        super("/login");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"login", "passHash"});
        this.userManager = DatabaseManager.getInstance().userManager;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        String login = body.get("login").getAsString();
        String passHash = body.get("passHash").getAsString();
        if (!userManager.loginExists(login)) {
            sendResponse(http, 400, "Пользователя с таким логином не существует");
            return;
        }
        AbstractMap.SimpleEntry<String, Boolean> result = userManager.login(login, passHash);
        if(result.getKey().equals("")){
            sendResponse(http, 400, "Неправильный логин или пароль");
            return;
        }

        JsonObject response = new JsonObject();
        response.add("token", new JsonPrimitive(result.getKey()));
        response.add("isAdmin", new JsonPrimitive(result.getValue()));
        sendResponse(http, 200, response.toString());
    }
}
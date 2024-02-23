package com.vvi.restaurantserver.server.endpoints.user;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.tables.UserManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class ChangeModeEndpoint extends BasicEndpoint {
    private final UserManager userManager;

    public ChangeModeEndpoint() {
        super("/user/changeMode");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequiredFields(new String[]{"login"})
                .setRequireToken()
                .setAdminOnly();
        this.userManager = DatabaseManager.getInstance().userManager;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        String login = body.get("login").getAsString();
        if (!userManager.changeMode(login)) {
            sendResponse(http, 400, "Невозможно изменить режим пользователя");
            return;
        }
        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
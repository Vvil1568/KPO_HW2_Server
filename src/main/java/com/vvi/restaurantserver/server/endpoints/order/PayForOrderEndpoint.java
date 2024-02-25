package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.OrderStatus;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.simulation.KitchenSimulator;

public class PayForOrderEndpoint extends BasicEndpoint {

    public PayForOrderEndpoint() {
        super("/order/payfororder");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        OrderStatus status = DatabaseManager.getInstance().orderManager.getOrderStatus(token);
        if(status!=OrderStatus.COOKED){
            sendResponse(http, 400, "Невозможно оплатить заказ, который "+status.name);
            return;
        }

        DatabaseManager.getInstance().orderManager.changeOrderStatus(token, OrderStatus.PAID);

        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
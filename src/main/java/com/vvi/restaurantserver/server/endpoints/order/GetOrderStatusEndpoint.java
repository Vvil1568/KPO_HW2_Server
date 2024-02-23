package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.OrderStatus;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;

public class GetOrderStatusEndpoint extends BasicEndpoint {

    public GetOrderStatusEndpoint() {
        super("/order/getstatus");
        new Builder(this)
                .setRequestMethod(RequestMethod.GET)
                .setRequireToken();
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        OrderStatus status = DatabaseManager.getInstance().orderManager.getOrderStatus(token);
        if(status==null){
            sendResponse(http, 400, "An error occurred during the order status request");
            return;
        }
        JsonObject response = new JsonObject();
        response.add("status",new JsonPrimitive("Заказ " +status.name));
        sendResponse(http, 200, response.toString());
    }
}
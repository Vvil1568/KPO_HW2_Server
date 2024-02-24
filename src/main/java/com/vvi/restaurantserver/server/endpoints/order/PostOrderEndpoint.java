package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.OrderStatus;
import com.vvi.restaurantserver.database.tables.OrderManager;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.simulation.KitchenSimulator;

public class PostOrderEndpoint extends BasicEndpoint {
    KitchenSimulator simulator;

    public PostOrderEndpoint(KitchenSimulator simulator) {
        super("/order/postorder");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequireToken();
        this.simulator = simulator;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        OrderManager manager = DatabaseManager.getInstance().orderManager;
        long time = manager.getCookingTime(token);
        if (time == -1) {
            sendResponse(http, 400, "An error occurred during the placement of order");
            return;
        }

        int order_id = manager.getCurrentOrderId(token);

        OrderStatus status = manager.getOrderStatus(token);
        if (status == null) {
            sendResponse(http, 400, "An error occurred during the placement of order");
            return;
        }

        if (status == OrderStatus.STARTED) {
            if (!manager.changeOrderStatus(order_id, OrderStatus.ACCEPTED)) {
                sendResponse(http, 400, "An error occurred during the placement of order");
                return;
            }
        }

        simulator.cookOrder(order_id, time);

        if (!manager.incrementLastPart(token)) {
            sendResponse(http, 400, "An error occurred during the placement of order");
            return;
        }

        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
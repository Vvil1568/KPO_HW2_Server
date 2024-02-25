package com.vvi.restaurantserver.server.endpoints.order;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.OrderStatus;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.RequestMethod;
import com.vvi.restaurantserver.simulation.KitchenSimulator;

public class CancelOrderEndpoint extends BasicEndpoint {

    KitchenSimulator simulator;

    public CancelOrderEndpoint(KitchenSimulator simulator) {
        super("/order/cancelorder");
        new Builder(this)
                .setRequestMethod(RequestMethod.POST)
                .setRequireToken();
        this.simulator = simulator;
    }

    @Override
    public void handle(HttpExchange http, JsonObject body) {
        OrderStatus status = DatabaseManager.getInstance().orderManager.getOrderStatus(token);
        if(status!=OrderStatus.ACCEPTED && status!=OrderStatus.PLACED){
            sendResponse(http, 400, "Невозможно отменить заказ, который "+status.name);
            return;
        }

        int orderId = DatabaseManager.getInstance().orderManager.getCurrentOrderId(token);
        simulator.cancelOrder(orderId);


        if(!DatabaseManager.getInstance().orderManager.removeOrder(token)){
            sendResponse(http, 400, "Ошибка при удалении заказа из базы данных");
            return;
        }
        JsonObject response = new JsonObject();
        sendResponse(http, 200, response.toString());
    }
}
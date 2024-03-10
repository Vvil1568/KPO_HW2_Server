package com.vvi.restaurantserver.server;

import com.sun.net.httpserver.HttpServer;
import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.server.endpoints.DefaultEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.comment.LeaveCommentEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.AddDishEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.EditDishEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.GetDishListEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.RemoveDishEndpoint;
import com.vvi.restaurantserver.server.endpoints.order.*;
import com.vvi.restaurantserver.server.endpoints.statistics.*;
import com.vvi.restaurantserver.server.endpoints.user.*;
import com.vvi.restaurantserver.simulation.KitchenSimulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RestaurantHttpServer {
    private HttpServer server;
    private KitchenSimulator simulator;

    public RestaurantHttpServer() {
    }

    public void initServer() {
        try {
            simulator = new KitchenSimulator();
            server = HttpServer.create(new InetSocketAddress(Config.getServerPort()), 0);
            addEndpoint(new DefaultEndpoint());
            addEndpoint(new RegistrationEndpoint());
            addEndpoint(new LoginEndpoint());
            addEndpoint(new AddDishEndpoint());
            addEndpoint(new RemoveDishEndpoint());
            addEndpoint(new GetDishListEndpoint());
            addEndpoint(new AddDishToOrderEndpoint());
            addEndpoint(new ChangeDishCountEndpoint());
            addEndpoint(new GetOrderStatusEndpoint());
            addEndpoint(new GetOrderDishListEndpoint());
            addEndpoint(new GetUserListEndpoint());
            addEndpoint(new ChangeModeEndpoint());
            addEndpoint(new LeaveCommentEndpoint());
            addEndpoint(new GetAllOrderedListEndpoint());
            addEndpoint(new PostOrderEndpoint(simulator));
            addEndpoint(new CancelOrderEndpoint(simulator));
            addEndpoint(new PayForOrderEndpoint());
            addEndpoint(new GetTotalRevenueEndpoint());
            addEndpoint(new GetOrderCountEndpoint());
            addEndpoint(new GetFavouriteDishEndpoint());
            addEndpoint(new GetCommentListEndpoint());
            addEndpoint(new GetRatingsEndpoint());
            addEndpoint(new EditDishEndpoint());
            server.setExecutor(null);
            server.start();
            System.out.println("Current server IP: " + getIp());
            System.out.println("Server successfully started on port " + Config.getServerPort());
        } catch (IOException e) {
            System.out.println("Cannot start http server on port " + Config.getServerPort());
        }
    }

    public static String getIp() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String ipAddress = localhost.getHostAddress();
            return ipAddress;
        } catch (Exception e) {
            return "";
        }
    }


    private void addEndpoint(BasicEndpoint endpoint) {
        server.createContext(endpoint.name, endpoint);
    }

    public void stopServer() {
        server.stop(0);
        simulator.stopKitchen();
    }
}

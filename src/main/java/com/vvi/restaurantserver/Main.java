package com.vvi.restaurantserver;

import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.server.RestaurantHttpServer;

public class Main {
    public static void main(String[] args) {
        if(!Config.initConfig("config.cfg")) return;
        RestaurantHttpServer server = new RestaurantHttpServer();
        try {
            server.initServer();
        } finally {
            server.stopServer();
        }
    }
}
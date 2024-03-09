package com.vvi.restaurantserver;

import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.RestaurantHttpServer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (!Config.initConfig("config.cfg")) return;
        if (!DatabaseManager.getInstance().init()) return;
        RestaurantHttpServer server = new RestaurantHttpServer();
        server.initServer();
        Scanner scanner = new Scanner(System.in);
        String command;
        do {
            command = scanner.next();
        } while (!command.equals("STOP"));
        server.stopServer();
        DatabaseManager.getInstance().shutdown();
    }
}
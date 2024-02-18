package com.vvi.restaurantserver.server;

import com.sun.net.httpserver.HttpServer;
import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.server.endpoints.DefaultEndpoint;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestaurantHttpServer {
    private static HttpServer server;

    public static void initServer(){
        try {
            server = HttpServer.create(new InetSocketAddress(Config.serverPort), 0);
            server.createContext("/", new DefaultEndpoint());
            server.setExecutor(null);
            server.start();
        }catch (IOException exception){
            System.out.println("There was an error during the server creation");
        }
    }
}

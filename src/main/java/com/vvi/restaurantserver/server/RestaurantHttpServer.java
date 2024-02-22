package com.vvi.restaurantserver.server;

import com.sun.net.httpserver.HttpServer;
import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.server.endpoints.DefaultEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.AddDishEndpoint;
import com.vvi.restaurantserver.server.endpoints.dish.GetDishList;
import com.vvi.restaurantserver.server.endpoints.dish.RemoveDish;
import com.vvi.restaurantserver.server.endpoints.user.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestaurantHttpServer {
    private HttpServer server;

    public RestaurantHttpServer() {
    }

    public void initServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(Config.getServerPort()), 0);
            addEndpoint(new DefaultEndpoint());
            addEndpoint(new RegistrationEndpoint());
            addEndpoint(new LoginEndpoint());
            addEndpoint(new AddDishEndpoint());
            addEndpoint(new RemoveDish());
            addEndpoint(new GetDishList());
            server.setExecutor(null);
            server.start();
        }catch(IOException e){
            System.out.println("Cannot start http server on port "+Config.getServerPort());
        }
    }

    private void addEndpoint(BasicEndpoint endpoint) {
        server.createContext(endpoint.name, endpoint);
    }

    public void stopServer() {
        server.stop(0);
    }
}

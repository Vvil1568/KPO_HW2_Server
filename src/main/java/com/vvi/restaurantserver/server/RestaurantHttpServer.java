package com.vvi.restaurantserver.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.server.endpoints.DefaultEndpoint;
import com.vvi.restaurantserver.server.endpoints.base.BasicEndpoint;
import com.vvi.restaurantserver.server.endpoints.user.LoginEndpoint;
import com.vvi.restaurantserver.server.endpoints.user.RegistrationEndpoint;

import javax.net.ssl.SSLParameters;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class RestaurantHttpServer {
    private HttpServer server;
    private final DatabaseManager databaseManager;

    public RestaurantHttpServer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(Config.getServerPort()), 0);//createServer();
            addEndpoint(new DefaultEndpoint());
            addEndpoint(new RegistrationEndpoint(databaseManager.userManager));
            addEndpoint(new LoginEndpoint(databaseManager.userManager));
            server.setExecutor(null);
            server.start();
        }catch(Exception e){

        }
    }

    private void addEndpoint(BasicEndpoint endpoint) {
        server.createContext(endpoint.name, endpoint);
    }

    public void stopServer() {
        server.stop(0);
    }
}

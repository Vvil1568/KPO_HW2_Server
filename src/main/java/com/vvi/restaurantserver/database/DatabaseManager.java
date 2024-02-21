package com.vvi.restaurantserver.database;

import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.database.tables.UserManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;
    public UserManager userManager;

    public boolean init() {
        final String url = "jdbc:mysql://" + Config.getDatabaseHost() + ":" + Config.getDatabasePort() + "/" + Config.getDatabaseName();
        try {
            connection = DriverManager.getConnection(url, Config.getDatabaseLogin(), Config.getDatabasePassword());
            userManager = new UserManager(connection);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean shutdown() {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}

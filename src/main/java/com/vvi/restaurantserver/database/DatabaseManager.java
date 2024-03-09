package com.vvi.restaurantserver.database;

import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.database.tables.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private Connection connection;
    public UserManager userManager;
    public DishManager dishManager;
    public OrderManager orderManager;
    public CommentManager commentManager;
    public StatisticsManager statisticsManager;

    private static DatabaseManager instance;

    public static DatabaseManager getInstance(){
        if(instance==null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    private DatabaseManager(){

    }

    public Connection getSQLiteConnection(File file) throws SQLException{
        final String url = "jdbc:sqlite:" + file.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        return connection;
    }

    public boolean init() {
        try {
            connection = getSQLiteConnection(new File("restaurantdb.db"));
            userManager = new UserManager(connection);
            dishManager = new DishManager(connection);
            orderManager = new OrderManager(connection);
            commentManager = new CommentManager(connection);
            statisticsManager = new StatisticsManager(connection);
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

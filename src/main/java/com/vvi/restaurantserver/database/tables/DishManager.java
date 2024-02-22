package com.vvi.restaurantserver.database.tables;

import com.vvi.restaurantserver.database.items.Dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DishManager {
    private final Connection connection;

    private final String DISH_LIST_QUERY = "SELECT * FROM dish;";
    private final String INSERT_DISH_QUERY = "INSERT INTO dish (name, description, price, time) VALUES (?,?,?,?);";
    private final String LAST_INSERT_ID_QUERY = "SELECT LAST_INSERT_ID();";
    private final String REMOVE_DISH_QUERY = "DELETE FROM dish WHERE id=?;";

    public DishManager(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Dish> getDishList() {
        try {
            PreparedStatement statement = connection.prepareStatement(DISH_LIST_QUERY);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Dish> result = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                long time = resultSet.getLong("time");
                result.add(new Dish(id, name, description, price, time));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getDishList query");
            return new ArrayList<>();
        }
    }

    public Dish addDish(Dish dish) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_DISH_QUERY);
            statement.setString(1, dish.getName());
            statement.setString(2, dish.getDescription());
            statement.setDouble(3, dish.getPrice());
            statement.setLong(4, dish.getTime());
            int result = statement.executeUpdate();
            if(result != 1){
                return dish;
            }
            statement = connection.prepareStatement(LAST_INSERT_ID_QUERY);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                dish.setId(resultSet.getInt(1));
                return dish;
            }

            return dish;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing addDish query");
        }
        return dish;
    }

    public boolean deleteDish(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(REMOVE_DISH_QUERY);
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing addDish query");
        }
        return false;
    }
}

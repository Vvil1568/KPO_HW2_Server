package com.vvi.restaurantserver.database.tables;

import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.database.items.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;

public class OrderManager {
    private final Connection connection;
    public OrderManager(Connection connection) {
        this.connection = connection;
    }
    private final String INSERT_ORDER_QUERY = "INSERT INTO `order` (post_timestamp, user_token, status) VALUES (?,?,?);";

    private final String LAST_ORDER_ID_QUERY = "SELECT order_id FROM `order` WHERE user_token = ? ORDER BY post_timestamp DESC LIMIT 1;";

    private final String UPDATE_ORDER_STATUS_QUERY = "UPDATE `order` SET status=? WHERE order_id=?;";
    private final String GET_ORDER_STATUS_QUERY = "SELECT status FROM `order` WHERE order_id=?;";
    private final String ADD_DISH_TO_ORDER_QUERY = "INSERT INTO order_to_dish (order_id, dish_id, count) VALUES (?,?,1) ON DUPLICATE KEY UPDATE count = count + 1;";

    private final String CHANGE_DISH_COUNT_QUERY = "UPDATE order_to_dish SET count = count + ? WHERE order_id = ? AND dish_id = ?;";

    private final String GET_DISH_COUNT_QUERY = "SELECT count FROM order_to_dish WHERE order_id = ? AND dish_id = ?;";

    private final String ORDER_DISH_LIST_QUERY = "SELECT dish.*, order_to_dish.count FROM order_to_dish " +
            "INNER JOIN dish ON order_to_dish.dish_id = dish.id WHERE order_to_dish.order_id=?;";

    private final String ALL_ORDERED_LIST_QUERY = "SELECT DISTINCT dish.* FROM order_to_dish " +
            "INNER JOIN dish ON order_to_dish.dish_id = dish.id " +
            "INNER JOIN `order` ON `order`.order_id = order_to_dish.order_id WHERE `order`.user_token=?;"; //TODO check status!
    private final String DELETE_DISH_QUERY = "DELETE FROM order_to_dish WHERE order_id = ? AND dish_id = ?;";

    public ArrayList<Dish> getAllOrderedList(String userToken) {
        try {
            PreparedStatement statement = connection.prepareStatement(ALL_ORDERED_LIST_QUERY);
            statement.setString(1, userToken);
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
            System.out.println("An error occurred while executing getAllOrderedList query");
            return new ArrayList<>();
        }
    }


    public ArrayList<AbstractMap.SimpleEntry<Dish, Integer>> getOrderDishList(String userToken) {
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing changeDishCount query");
            return new ArrayList<>();
        }
        try {
            PreparedStatement statement = connection.prepareStatement(ORDER_DISH_LIST_QUERY);
            statement.setInt(1, curOrderId);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<AbstractMap.SimpleEntry<Dish, Integer>> result = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                long time = resultSet.getLong("time");
                int count = resultSet.getInt("count");
                result.add(new AbstractMap.SimpleEntry<>(new Dish(id, name, description, price, time), count));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getOrderDishList query");
            return new ArrayList<>();
        }
    }

    public int changeDishCount(String userToken, int dishId, int delta){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing changeDishCount query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(CHANGE_DISH_COUNT_QUERY);
            statement.setInt(1, delta);
            statement.setInt(2, curOrderId);
            statement.setInt(3, dishId);
            int result = statement.executeUpdate();
            if(result == 1){
                int dishCount = getDishCount(userToken, dishId);
                if(dishCount<=0){
                    removeDishFromOrder(userToken, dishId);
                    return 0;
                }
                return dishCount;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing changeDishCount query");
        }
        return -1;
    }

    public int getDishCount(String userToken, int dishId){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing getDishCount query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_DISH_COUNT_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getDishCount query");
        }
        return -1;
    }

    public OrderStatus getOrderStatus(String userToken){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing getOrderStatus query");
            return null;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_ORDER_STATUS_QUERY);
            statement.setInt(1, curOrderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return  OrderStatus.values()[resultSet.getInt(1)];
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getOrderStatus query");
        }
        return null;
    }

    public boolean addDishToOrder(String userToken, int dishId){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1 && !startOrder(userToken)){
            System.out.println("An error occurred while executing addDishToOrder query");
            return false;
        }
        curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing addDishToOrder query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(ADD_DISH_TO_ORDER_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing addDishToOrder query");
        }
        return false;
    }

    private boolean removeDishFromOrder(String userToken, int dishId){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing removeDishToOrder query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_DISH_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            int result = statement.executeUpdate();
            if(result == 1){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing removeDishToOrder query");
        }
        return false;
    }


    public boolean changeOrderStatus(String userToken, OrderStatus status){
        int curOrderId = getCurrentOrderId(userToken);
        if(curOrderId==-1){
            System.out.println("An error occurred while executing changeOrderStatus query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_ORDER_STATUS_QUERY);
            statement.setInt(1, status.ordinal());
            statement.setInt(2, curOrderId);
            int result = statement.executeUpdate();
            if(result == 1){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing changeOrderStatus query");
        }
        return false;
    }

    private boolean startOrder(String userToken){
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_ORDER_QUERY);
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, userToken);
            statement.setInt(3, OrderStatus.STARTED.ordinal());
            int result = statement.executeUpdate();
            if(result == 1){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing startOrder query");
        }
        return false;
    }

    private int getCurrentOrderId(String userToken){
        try {
            PreparedStatement statement = connection.prepareStatement(LAST_ORDER_ID_QUERY);
            statement.setString(1, userToken);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ignored) {}
        return -1;
    }
}

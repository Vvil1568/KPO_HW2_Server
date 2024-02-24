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

    private final String INSERT_ORDER_QUERY = "INSERT INTO `order` (post_timestamp, user_token, status, last_part) VALUES (?,?,?,0);";

    private final String LAST_ORDER_ID_QUERY = "SELECT order_id FROM `order` WHERE user_token = ? AND status<4 ORDER BY post_timestamp DESC LIMIT 1;"; //TODO don't allow changes after ready to pay

    private final String UPDATE_ORDER_STATUS_QUERY = "UPDATE `order` SET status=? WHERE order_id=?;";
    private final String GET_ORDER_STATUS_QUERY = "SELECT status FROM `order` WHERE order_id=?;";

    private final String ALL_ORDERED_LIST_QUERY = "SELECT DISTINCT dish.* FROM order_to_dish " +
            "INNER JOIN dish ON order_to_dish.dish_id = dish.id " +
            "INNER JOIN `order` ON `order`.order_id = order_to_dish.order_id WHERE `order`.user_token=? AND `order`.status=4;";

    private final String GET_LAST_PART_QUERY = "SELECT last_part FROM `order` WHERE order_id=?;";

    private final String ADD_DISH_TO_ORDER_QUERY = "INSERT INTO order_to_dish (order_id, dish_id, order_part, count) " +
            "VALUES (?,?,?,1) ON DUPLICATE KEY UPDATE count = count + 1;";

    private final String DELETE_DISH_QUERY = "DELETE FROM order_to_dish WHERE order_id = ? AND dish_id = ? AND order_part = ?;";

    private final String CHANGE_DISH_COUNT_QUERY = "UPDATE order_to_dish SET count = count + ? WHERE " +
            "order_id = ? AND dish_id = ? AND order_part = ?;";

    private final String GET_DISH_COUNT_QUERY = "SELECT count FROM order_to_dish WHERE order_id = ? AND dish_id = ? AND order_part = ?;";

    private final String ORDER_DISH_LIST_QUERY = "SELECT dish.*, order_to_dish.count FROM order_to_dish " +
            "INNER JOIN dish ON order_to_dish.dish_id = dish.id WHERE order_to_dish.order_id=? AND order_part = ?;";

    private final String GET_COOKING_TIME_QUERY =
            "SELECT sum(dish.time*order_to_dish.count) FROM order_to_dish " +
            "INNER JOIN dish ON order_to_dish.dish_id = dish.id " +
            "INNER JOIN `order` ON `order`.order_id = order_to_dish.order_id " +
            "WHERE `order`.order_id=? AND order_to_dish.order_part=`order`.last_part; ";

    private final String INCREMENT_LAST_PART_QUERY = "UPDATE `order` SET last_part=last_part+1 WHERE order_id=?;";


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
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing getOrderDishList query");
            return new ArrayList<>();
        }
        int curPart = getLastPart(userToken);
        if (curPart==-1) {
            System.out.println("An error occurred while executing getOrderDishList query");
            return new ArrayList<>();
        }
        try {
            PreparedStatement statement = connection.prepareStatement(ORDER_DISH_LIST_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, curPart);
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

    public int changeDishCount(String userToken, int dishId, int delta) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing changeDishCount query");
            return -1;
        }
        int curPart = getLastPart(userToken);
        if (curPart==-1) {
            System.out.println("An error occurred while executing changeDishCount query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(CHANGE_DISH_COUNT_QUERY);
            statement.setInt(1, delta);
            statement.setInt(2, curOrderId);
            statement.setInt(3, dishId);
            statement.setInt(4, curPart);
            int result = statement.executeUpdate();
            if (result == 1) {
                int dishCount = getDishCount(userToken, dishId);
                if (dishCount <= 0) {
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

    public int getDishCount(String userToken, int dishId) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing getDishCount query");
            return -1;
        }
        int curPart = getLastPart(userToken);
        if (curPart==-1) {
            System.out.println("An error occurred while executing getDishCount query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_DISH_COUNT_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            statement.setInt(3, curPart);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getDishCount query");
        }
        return -1;
    }

    public OrderStatus getOrderStatus(String userToken) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing getOrderStatus query");
            return null;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_ORDER_STATUS_QUERY);
            statement.setInt(1, curOrderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return OrderStatus.values()[resultSet.getInt(1)];
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getOrderStatus query");
        }
        return null;
    }

    public boolean addDishToOrder(String userToken, int dishId) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1 && !startOrder(userToken)) {
            System.out.println("An error occurred while executing addDishToOrder query");
            return false;
        }
        curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing addDishToOrder query");
            return false;
        }
        int curPart = getLastPart(userToken);
        if (curPart==-1) {
            System.out.println("An error occurred while executing addDishToOrder query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(ADD_DISH_TO_ORDER_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            statement.setInt(3, curPart);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing addDishToOrder query");
        }
        return false;
    }

    private boolean removeDishFromOrder(String userToken, int dishId) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing removeDishToOrder query");
            return false;
        }
        int curPart = getLastPart(userToken);
        if (curPart==-1) {
            System.out.println("An error occurred while executing removeDishToOrder query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_DISH_QUERY);
            statement.setInt(1, curOrderId);
            statement.setInt(2, dishId);
            statement.setInt(3, curPart);
            int result = statement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing removeDishToOrder query");
        }
        return false;
    }

    public boolean changeOrderStatus(int order_id, OrderStatus status) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_ORDER_STATUS_QUERY);
            statement.setInt(1, status.ordinal());
            statement.setInt(2, order_id);
            int result = statement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing changeOrderStatus query");
        }
        return false;
    }

    public boolean changeOrderStatus(String userToken, OrderStatus status) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing changeOrderStatus query");
            return false;
        }
        return changeOrderStatus(curOrderId, status);
    }

    private boolean startOrder(String userToken) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_ORDER_QUERY);
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, userToken);
            statement.setInt(3, OrderStatus.STARTED.ordinal());
            int result = statement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing startOrder query");
        }
        return false;
    }

    public int getCurrentOrderId(String userToken) {
        try {
            PreparedStatement statement = connection.prepareStatement(LAST_ORDER_ID_QUERY);
            statement.setString(1, userToken);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return -1;
    }

    private int getLastPart(String userToken) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing getLastPart query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_LAST_PART_QUERY);
            statement.setInt(1, curOrderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public long getCookingTime(String userToken) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing getCookingTime query");
            return -1;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(GET_COOKING_TIME_QUERY);
            statement.setInt(1, curOrderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public boolean incrementLastPart(String userToken) {
        int curOrderId = getCurrentOrderId(userToken);
        if (curOrderId == -1) {
            System.out.println("An error occurred while executing incrementLastPart query");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(INCREMENT_LAST_PART_QUERY);
            statement.setInt(1, curOrderId);
            int result = statement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException ignored) {
            System.out.println("An error occurred while executing incrementLastPart query");
        }
        return false;
    }
}

package com.vvi.restaurantserver.database.tables;

import com.vvi.restaurantserver.database.items.Comment;
import com.vvi.restaurantserver.database.items.Dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;

public class StatisticsManager {

    private final Connection connection;
    private final String TOTAL_REVENUE_QUERY = "SELECT sum(order_to_dish.count*dish.price) FROM `order` JOIN order_to_dish ON `order`.order_id = order_to_dish.order_id JOIN dish ON dish.id=order_to_dish.dish_id WHERE `order`.post_timestamp<=? AND `order`.post_timestamp>=?;";

    private final String COUNT_ORDER_QUERY = "SELECT count(1) FROM `order` WHERE `order`.post_timestamp<=? AND `order`.post_timestamp>=?;";

    private final String FAVOURITE_DISH_QUERY =
            "SELECT dish.name FROM ((SELECT sum(order_to_dish.count) as cnt, dish_id " +
            "FROM order_to_dish JOIN `order` ON order_to_dish.order_id = `order`.order_id  " +
            "WHERE `order`.post_timestamp<=? AND `order`.post_timestamp>=? GROUP BY dish_id) AS counts) " +
            "JOIN dish ON counts.dish_id=dish.id ORDER BY cnt DESC LIMIT 1;";

    private final String GET_COMMENTS_QUERY = "SELECT dish.name, body, stars FROM comments JOIN dish ON dish.id=comments.dish_id  WHERE comments.post_timestamp<=? AND comments.post_timestamp>=?;";

    private final String GET_RATINGS_QUERY = "SELECT dish.name, avg(comments.stars) FROM comments JOIN dish ON dish.id=comments.dish_id WHERE comments.post_timestamp<=? AND comments.post_timestamp>=? GROUP BY comments.dish_id;";

    public StatisticsManager(Connection connection) {
        this.connection = connection;
    }


    public ArrayList<Comment> getCommentList(long timeFrom, long timeTo) {
        try {
            PreparedStatement statement = connection.prepareStatement(GET_COMMENTS_QUERY);
            statement.setLong(1, timeTo);
            statement.setLong(2, timeFrom);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Comment> result = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String body = resultSet.getString("body");
                int stars = resultSet.getInt("stars");
                result.add(new Comment(name, stars, body));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getCommentList query");
            return new ArrayList<>();
        }
    }

    public ArrayList<AbstractMap.SimpleEntry<String, Double>> getRatings(long timeFrom, long timeTo) {
        try {
            PreparedStatement statement = connection.prepareStatement(GET_RATINGS_QUERY);
            statement.setLong(1, timeTo);
            statement.setLong(2, timeFrom);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<AbstractMap.SimpleEntry<String, Double>> result = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                Double rating = resultSet.getDouble(2);
                result.add(new AbstractMap.SimpleEntry<>(name, rating));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getRatings query");
            return new ArrayList<>();
        }
    }


    public double getTotalRevenue(long timeFrom, long timeTo) {
        try {
            PreparedStatement statement = connection.prepareStatement(TOTAL_REVENUE_QUERY);
            statement.setLong(1, timeTo);
            statement.setLong(2, timeFrom);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getTotalRevenue query");
        }
        return -1;
    }

    public int getOrderCount(long timeFrom, long timeTo) {
        try {
            PreparedStatement statement = connection.prepareStatement(COUNT_ORDER_QUERY);
            statement.setLong(1, timeTo);
            statement.setLong(2, timeFrom);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getOrderCount query");
        }
        return -1;
    }

    public String getFavouriteDish(long timeFrom, long timeTo) {
        try {
            PreparedStatement statement = connection.prepareStatement(FAVOURITE_DISH_QUERY);
            statement.setLong(1, timeTo);
            statement.setLong(2, timeFrom);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getFavouriteDish query");
        }
        return "";
    }
}

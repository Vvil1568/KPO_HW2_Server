package com.vvi.restaurantserver.database.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommentManager {
    private final Connection connection;

    private final String INSERT_COMMENT_QUERY = "INSERT INTO comments (token, dish_id, post_timestamp, body, stars) VALUES (?,?,?,?,?);";

    public CommentManager(Connection connection) {
        this.connection = connection;
    }

    public boolean addComment(String token, int dishId, String body, int stars) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_COMMENT_QUERY);
            statement.setString(1, token);
            statement.setInt(2, dishId);
            statement.setLong(3, System.currentTimeMillis());
            statement.setString(4, body);
            statement.setInt(5, stars);
            int result = statement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing addComment query");
        }
        return false;
    }
}

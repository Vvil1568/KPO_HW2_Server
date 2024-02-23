package com.vvi.restaurantserver.database.tables;

import com.vvi.restaurantserver.database.items.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;

public class UserManager {
    private final Connection connection;

    private final String USER_LIST_QUERY = "SELECT * FROM user WHERE token<>?;";
    private final String LOGIN_QUERY = "SELECT token, is_admin FROM user WHERE login=? AND pass_hash=?;";
    private final String LOGIN_EXISTS_QUERY = "SELECT EXISTS(SELECT * FROM user WHERE login=?);";
    private final String REGISTER_QUERY = "INSERT INTO user (token, fio, login, pass_hash, is_admin) VALUES (?,?,?,?,?);";
    private final String HAS_USERS_QUERY = "SELECT EXISTS(SELECT 1 FROM user);";
    private final String IS_VALID_USER_QUERY = "SELECT EXISTS(SELECT * FROM user WHERE token=?);";
    private final String IS_ADMIN_QUERY = "SELECT EXISTS(SELECT * FROM user WHERE token=? AND is_admin=1);";
    private final String CHANGE_MODE_QUERY = "UPDATE user SET is_admin = CASE WHEN is_admin = 1 THEN 0 ELSE 1 END WHERE login = ?;";
    public UserManager(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<User> getUserList(String token) {
        try {
            PreparedStatement statement = connection.prepareStatement(USER_LIST_QUERY);
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<User> result = new ArrayList<>();
            while (resultSet.next()) {
                String fio = resultSet.getString("fio");
                String login = resultSet.getString("login");
                boolean isAdmin = resultSet.getBoolean("is_admin");
                result.add(new User(fio, login, isAdmin));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing getUserList query");
            return new ArrayList<>();
        }
    }

    public boolean hasUsers() {
        try {
            PreparedStatement statement = connection.prepareStatement(HAS_USERS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing hasUsers query");
        }
        return false;
    }

    public boolean loginExists(String login) {
        try {
            PreparedStatement statement = connection.prepareStatement(LOGIN_EXISTS_QUERY);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing loginExists query");
        }
        return false;
    }

    public boolean isValidUser(String token) {
        try {
            PreparedStatement statement = connection.prepareStatement(IS_VALID_USER_QUERY);
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing isValidUser query");
        }
        return false;
    }

    public boolean isAdmin(String token) {
        try {
            PreparedStatement statement = connection.prepareStatement(IS_ADMIN_QUERY);
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing isAdmin query");
        }
        return false;
    }

    public AbstractMap.SimpleEntry<String, Boolean> login(String login, String passHash) {
        try {
            PreparedStatement statement = connection.prepareStatement(LOGIN_QUERY);
            statement.setString(1, login);
            statement.setString(2, passHash);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String token = resultSet.getString(1);
                Boolean isAdmin = resultSet.getBoolean(2);
                return new AbstractMap.SimpleEntry<>(token, isAdmin);
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing login query");
        }
        return new AbstractMap.SimpleEntry<>("", false);
    }

    public boolean changeMode(String login) {
        try {
            PreparedStatement statement = connection.prepareStatement(CHANGE_MODE_QUERY);
            statement.setString(1, login);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing changeMode query");
        }
        return false;
    }
    public boolean register(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement(REGISTER_QUERY);
            statement.setString(1, user.getToken());
            statement.setString(2, user.getFio());
            statement.setString(3, user.getLogin());
            statement.setString(4, user.getPassHash());
            statement.setBoolean(5, user.isAdmin());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("An error occurred while executing register query");
        }
        return false;
    }
}

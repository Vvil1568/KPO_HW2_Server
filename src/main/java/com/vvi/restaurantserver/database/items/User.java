package com.vvi.restaurantserver.database.items;

import java.util.UUID;

public class User {
    private final String token;
    private final String fio;
    private final String login;
    private final String passHash;
    private final boolean isAdmin;

    public User(String fio, String login, boolean isAdmin) {
        this("", fio, login, "", isAdmin);
    }

    public User(String fio, String login, String passHash, boolean isAdmin) {
        this(generateToken(login), fio, login, passHash, isAdmin);
    }

    private User(String token, String fio, String login, String passHash, boolean isAdmin) {
        this.token = token;
        this.fio = fio;
        this.login = login;
        this.passHash = passHash;
        this.isAdmin = isAdmin;
    }

    public String getToken() {
        return token;
    }

    public String getFio() {
        return fio;
    }

    public String getLogin() {
        return login;
    }

    public String getPassHash() {
        return passHash;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    private static String generateToken(String login) {
        String salt = "SUGAR"; //TODO better salt
        return String.valueOf(UUID.nameUUIDFromBytes((login + salt).getBytes()));
    }
}

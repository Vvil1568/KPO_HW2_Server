package com.vvi.restaurantserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class Config {
    private static int serverPort;
    private static String databaseHost;
    private static int databasePort;
    private static String databaseName;
    private static String databaseLogin;
    private static String databasePassword;

    public static boolean initConfig(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            if (!genConfigTemplate(fileName)) return false;
            System.out.println("Created a new config file.");
            System.out.println("Please fill out the config and restart the server.");
            return false;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException exception) {
            System.out.println("Cannot read the contents of a config file");
            return false;
        }
        try {
            serverPort = getIntProperty(lines, "Server port");
            databaseHost = getStrProperty(lines, "Database host");
            databasePort = getIntProperty(lines, "Database port");
            databaseName = getStrProperty(lines, "Database name");
            databaseLogin = getStrProperty(lines, "Database login");
            databasePassword = getStrProperty(lines, "Database password");
        } catch (NumberFormatException exception) {
            System.out.println("Can't parse \"" + exception.getMessage() + "\" config entry: the value should be integer!");
            return false;
        } catch (NoSuchElementException exception) {
            System.out.println("Can't find config entry \"" + exception + "\"!");
            return false;
        }
        return true;
    }

    public static int getIntProperty(List<String> lines, String name) {
        String value = getStrProperty(lines, name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new NumberFormatException(name);
        }
    }

    public static String getStrProperty(List<String> lines, String name) {
        String line = "";
        for (String l : lines) {
            if (l.startsWith(name)) {
                line = l;
                break;
            }
        }
        if (line.isEmpty()) {
            throw new NoSuchElementException(name);
        }
        return line.substring(name.length() + 1).trim();
    }

    public static boolean genConfigTemplate(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            List<String> defaultConfig = Arrays.asList(
                    "====Server====",
                    "Server port:",
                    "===DataBase===",
                    "Database host:",
                    "Database port:",
                    "Database name:",
                    "Database login:",
                    "Database password:"
            );
            Files.write(file.toPath(), defaultConfig, StandardCharsets.UTF_8);
            return true;
        } catch (IOException exception) {
            System.out.println("Can not create a default configuration file!");
            return false;
        }
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String getDatabaseHost() {
        return databaseHost;
    }

    public static String getDatabaseLogin() {
        return databaseLogin;
    }

    public static int getDatabasePort() {
        return databasePort;
    }

    public static String getDatabasePassword() {
        return databasePassword;
    }

    public static String getDatabaseName() {
        return databaseName;
    }
}

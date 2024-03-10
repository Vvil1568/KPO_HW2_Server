package com.vvi.restaurantserver.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;

public class Config {
    private static int serverPort;
    private static int cooksCount;

    public static boolean initConfig(String fileName) {
        File file = new File(fileName);
        if (!file.exists() && !genConfigTemplate(fileName)) {
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
            cooksCount = getIntProperty(lines, "Cooks count");
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
            try (InputStream defaultConfig = Config.class.getClassLoader().getResourceAsStream("presets/config.cfg")) {
                Files.copy(defaultConfig, file.toPath());
            }
            return true;
        } catch (IOException exception) {
            System.out.println("Can not create a default configuration file!");
            return false;
        }
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static int getCooksCount() {
        return cooksCount;
    }
}

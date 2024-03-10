package com.vvi.restaurantserver.database.items;

public class Dish {
    private int id;
    private final String name;
    private final String description;
    private final double price;
    private final long time;

    private final String image;

    public Dish(String name, String description, double price, long time, String image) {
        this(-1, name, description, price, time, image);
    }

    public Dish(String name, String description, double price, long time) {
        this(-1, name, description, price, time, "");
    }

    public Dish(int id, String name, String description, double price, long time, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }
}

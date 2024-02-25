package com.vvi.restaurantserver.database.items;

public class Comment {
    private String comment;
    private int stars;
    private String name;

    public Comment(String name, int stars, String comment){
        this.name = name;
        this.stars = stars;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public int getStars() {
        return stars;
    }

    public String getName() {
        return name;
    }

}


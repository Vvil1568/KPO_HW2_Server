package com.vvi.restaurantserver.utils;

import com.google.gson.JsonObject;
import com.vvi.restaurantserver.database.items.Comment;
import com.vvi.restaurantserver.database.items.Dish;
import com.vvi.restaurantserver.database.items.User;

public class JsonSerialisationUtils {
    public static JsonObject toJson(Dish dish){
        JsonObject dishObj = new JsonObject();
        dishObj.addProperty("id",dish.getId());
        dishObj.addProperty("name",dish.getName());
        dishObj.addProperty("desc",dish.getDescription());
        dishObj.addProperty("price",dish.getPrice());
        dishObj.addProperty("time",dish.getTime());
        return dishObj;
    }

    public static JsonObject toJson(User user){
        JsonObject userObj = new JsonObject();
        userObj.addProperty("login",user.getLogin());
        userObj.addProperty("fio",user.getFio());
        userObj.addProperty("isAdmin",user.isAdmin());
        return userObj;
    }

    public static JsonObject toJson(Comment comment){
        JsonObject userObj = new JsonObject();
        userObj.addProperty("name",comment.getName());
        userObj.addProperty("stars",comment.getStars());
        userObj.addProperty("body",comment.getComment());
        return userObj;
    }
}

package com.vvi.restaurantserver.utils;

import com.google.gson.JsonObject;
import com.vvi.restaurantserver.database.items.Dish;

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
}

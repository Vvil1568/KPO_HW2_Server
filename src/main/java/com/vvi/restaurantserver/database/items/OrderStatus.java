package com.vvi.restaurantserver.database.items;

public enum OrderStatus {
    STARTED("составляется"),
    PLACED("готовится"),
    COOKED("готов и ожидает оплаты"),
    PAID("оплачен");

    public final String name;

    OrderStatus(String name){
        this.name = name;
    }
}

package com.vvi.restaurantserver.simulation;

import com.vvi.restaurantserver.config.Config;
import com.vvi.restaurantserver.database.DatabaseManager;
import com.vvi.restaurantserver.database.items.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KitchenSimulator {
    private final ExecutorService cooks;
    private final HashMap<Integer, ArrayList<Future<Void>>> orders;
    public KitchenSimulator(){
        cooks = Executors.newFixedThreadPool(Config.getCooksCount());
        orders = new HashMap<>();
    }

    public void cookOrder(int order_id, long time){
        ArrayList<Future<Void>> oldList = orders.computeIfAbsent(order_id,(id) -> new ArrayList<>());
        final int oldCurId = oldList.size();
        Future<?> future = cooks.submit(()->{
            int curId = oldCurId;
            System.out.println("Started to work on "+curId+" part of "+order_id+" order!");
            try{
                DatabaseManager.getInstance().orderManager.changeOrderStatus(order_id, OrderStatus.PLACED);
                Thread.sleep(time);
                ArrayList<Future<Void>> list = orders.get(order_id);
                list.remove(curId);
                System.out.println("Finished "+curId+" part of "+order_id+" order!");
                if(list.isEmpty()) {
                    DatabaseManager.getInstance().orderManager.changeOrderStatus(order_id, OrderStatus.COOKED);
                    orders.remove(order_id);
                    System.out.println("Finished "+order_id+" order!");
                }
            }catch (InterruptedException ignored){
                System.out.println("Stopped working on "+curId+" part of "+order_id+" order!");
            }
        });
        orders.computeIfAbsent(order_id,(id) -> new ArrayList<>()).add((Future<Void>) future);
    }

    public void cancelOrder(int order_id){
        if(!orders.containsKey(order_id)) return;
        for(Future<Void> future: orders.get(order_id)){
            future.cancel(true);
        }
        orders.remove(order_id);
    }

    public void stopKitchen(){
        cooks.shutdown();
    }
}

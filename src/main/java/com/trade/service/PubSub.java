package com.trade.service;

import com.trade.models.Order;
import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;

public class PubSub implements Runnable{
    private Jedis jedis = null;
    @Override
    public void run() {
//        todo: Get list of validated orders, published by the order validated.
        try {
//            Jedis object for connecting to the redis-server
            jedis = (new JedisConnection()).getConnection();

//            creating JedisPubSub object for subscribing to channels
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("\nPrinting published message.");
                    System.out.println(message + "\n");
                    List<Order> validatedOrders = (List) ObjectSerializer.unserizlize(Base64.getDecoder().decode(message.getBytes()));

                    assert validatedOrders != null;
                    // TODO: 11/24/20 Remove the code below doesn't do anything solid.
                    validatedOrders.forEach(validatedOrder -> {
                        System.out.println(validatedOrder.toString());
                        pushDataToQueue(validatedOrder);
                    });
                }
            };



            while (true) {
                jedis.subscribe(jedisPubSub, "C1");
            }
        } catch (Exception e) {
            System.out.println("Exception message is: " + e.getMessage());
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    private void pushDataToQueue(Order order) {
        Jedis jedis = null;
        try {
            jedis = (new JedisConnection()).getConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        jedis.lpush("makeOrder", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(order)));
        System.out.println("List pushed to makeOrder queue");
    }
}

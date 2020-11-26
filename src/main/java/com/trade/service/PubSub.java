package com.trade.service;

import com.trade.models.Order;
import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;

public class PubSub implements Runnable {
    private Jedis jedis = null;
    @Override
    public void run() {
        try {
//            Jedis object for connecting to the redis-server
            jedis = (new JedisConnection()).getConnection();

//            creating JedisPubSub object for subscribing to channels
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                List<Order> validatedOrders = (List) ObjectSerializer.unserizlize(Base64.getDecoder().decode(message.getBytes()));
                assert validatedOrders != null;
                validatedOrders.forEach(validatedOrder -> {
                    pushDataToQueue(validatedOrder);
                });
                }
            };
            while (true) {
                String ORDER_TO_TRADE_ENGINE_CHANNEL = "C1";
                jedis.subscribe(jedisPubSub, ORDER_TO_TRADE_ENGINE_CHANNEL);
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
    }
}

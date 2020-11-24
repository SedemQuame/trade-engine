package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import com.trade.utility.Utility;
import redis.clients.jedis.Jedis;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;

public class MakeOrder implements Runnable {
    private Jedis jedis = null;

    @Override
    public void run() {
        try {
            jedis = (new JedisConnection()).getConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        while (true) {
            String data = jedis.rpop("makeOrder");

            if (data == null) continue;

            System.out.println("\nPopped data from the makeOrder queue: \n" + data);

            byte[] byteData = Base64.getDecoder().decode(data.getBytes());
            Order validatedOrder = (Order) ObjectSerializer.unserizlize(byteData);

            // TODO: 11/24/20 Remove
            System.out.println("\nValidated Order: \n " + validatedOrder.toString());

//            jedis.set(validatedOrder.id,data);
            jedis.lpush("monitorQueue", data);

            OrderBookRequest orderBookRequest = new OrderBookRequest(
                    validatedOrder.getOrderId(),
                    validatedOrder.getTicker(),
                    validatedOrder.getSide()
            );

            System.out.println("Order Book Request => " + orderBookRequest.toString());

            String requestString = Utility.convertToString(orderBookRequest);
            jedis.lpush("exchange1OrderRequest", requestString);
            jedis.lpush("exchange2OrderRequest", requestString);
        }
    }
}

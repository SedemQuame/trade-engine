package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import com.trade.utility.Utility;
import redis.clients.jedis.Jedis;

import java.net.URISyntaxException;
import java.util.*;

public class MonitorQueue implements Runnable {
    private Jedis jedis = null;

    @Override
    public void run() {
        try {
            jedis = (new JedisConnection()).getConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        while (true) {
            String data = jedis.rpop("monitorQueue");

            if (data == null) continue;
            System.out.println("\nMonitor Queue");
            System.out.println(data);

            byte[] byteData = Base64.getDecoder().decode(data.getBytes());
            Order validatedOrder = (Order) ObjectSerializer.unserizlize(byteData);

            System.out.println("\nDeserialized data");
            System.out.println(validatedOrder.toString());

            if (jedis.llen(data + "orderbook") == 2) {
                // TODO: 11/20/20 Serialise objects using ObjectSerializer class,
                //  instead of the Utility.convertToObject methods, and remove the Utility Class.
                PendingOrder[] pendingOrderList1 = Utility.convertToObject(jedis.rpop(data + "orderbook"), PendingOrder[].class);
                PendingOrder[] pendingOrderList2 = Utility.convertToObject(jedis.rpop(data + "orderbook"), PendingOrder[].class);
                /*
                 * Logic
                 * Get Exchange orders into one list
                 * Sort list by price
                 * if (side==Buy){
                 * sort  Asc
                 * else
                 * sort desc
                 * Start popping from top till order quantity is fulfilled
                 * }
                 *  */

                //Exchange Orders into one list
                List<PendingOrder> pendingOrderList = new ArrayList(Arrays.asList(pendingOrderList1, pendingOrderList2));

                //sorting
                if (validatedOrder.getSide().toLowerCase() == "buy") {
                    pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice));
                } else {
                    pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice).reversed());
                }

                //Popping and fulfilling orders
                PendingOrder curExchangeOrder;
                int availableQty, buffer = 0;
                PartialOrder partialOrder = null;
                int targetQty = Integer.valueOf(validatedOrder.getQuantity());

                while (targetQty > 0) {
                    curExchangeOrder = pendingOrderList.remove(0);
                    availableQty = curExchangeOrder.quantity - curExchangeOrder.cumulativeQuantity;
                    if (availableQty >= targetQty) {
                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
                        targetQty = 0;
                        jedis.lpush("makeOrder" + curExchangeOrder.exchange, partialOrder.toString());
                    } else if (pendingOrderList.isEmpty()) {
                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
                        jedis.lpush("monitorQueue", partialOrder.toString());
                    } else {
                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
                        System.out.println(partialOrder.toString());
                        targetQty -= availableQty;
                        jedis.lpush("makeOrder" + curExchangeOrder.exchange, partialOrder.toString());
                    }
                }
            } else {
                jedis.lpush("monitorQueue", data);
            }
        }
    }
}

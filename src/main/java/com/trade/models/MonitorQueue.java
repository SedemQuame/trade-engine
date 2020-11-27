package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
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
            byte[] byteData = Base64.getDecoder().decode(data.getBytes());
            Order validatedOrder = (Order) ObjectSerializer.unserizlize(byteData);


//            if (jedis.llen(validatedOrder.getOrderId() + "orderbook") == 2) {
//                // TODO: 11/20/20 Serialise objects using ObjectSerializer class,
//                //  instead of the Utility.convertToObject methods, and remove the Utility Class.
////                PendingOrder[] pendingOrderList1 = Utility.convertToObject(jedis.rpop(validatedOrder.getOrderId()+ "orderbook"), PendingOrder[].class);
////                PendingOrder[] pendingOrderList2 = Utility.convertToObject(jedis.rpop(validatedOrder.getOrderId() + "orderbook"), PendingOrder[].class);
//
//                PendingOrder[] pendingOrderList1 = (PendingOrder[]) ObjectSerializer.unserizlize(Base64.getDecoder().decode(jedis.rpop(validatedOrder.getOrderId()+ "orderbook")));
//                PendingOrder[] pendingOrderList2 = (PendingOrder[]) ObjectSerializer.unserizlize(Base64.getDecoder().decode(jedis.rpop(validatedOrder.getOrderId() + "orderbook")));
//
//                //Exchange Orders into one list
//                List<PendingOrder> pendingOrderList = new ArrayList(Arrays.asList(pendingOrderList1, pendingOrderList2));
//
//                //sorting
//                if (validatedOrder.getSide().toLowerCase().equals("buy")) {
//                    pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice));
//                } else {
//                    pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice).reversed());
//                }
//
//                //Popping and fulfilling orders
//                PendingOrder curExchangeOrder = null;
//                int availableQty, targetQty = validatedOrder.getQuantity();
//                PartialOrder partialOrder = null;
//
//                while (targetQty > 0) {
//                    curExchangeOrder = pendingOrderList.remove(0);
//                    System.out.println("Current Exchange Order: " + curExchangeOrder.toString());
//                    availableQty = curExchangeOrder.quantity - curExchangeOrder.cumulativeQuantity;
//                    if (availableQty >= targetQty) {
//                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), curExchangeOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
//                        targetQty = 0;
//                        jedis.lpush("makeOrder" + curExchangeOrder.exchange, Base64.getEncoder().encodeToString(ObjectSerializer.serialize(partialOrder)));
//                    } else if (pendingOrderList.isEmpty()) {
//                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
//                        jedis.lpush("monitorQueue", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(partialOrder)));
//                    } else {
//                        partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), String.valueOf(targetQty), validatedOrder.getSide());
//                        System.out.println(partialOrder.toString());
//                        targetQty -= availableQty;
//                        jedis.lpush("makeOrder" + curExchangeOrder.exchange, Base64.getEncoder().encodeToString(ObjectSerializer.serialize(partialOrder)));
//                    }
//                }
//            } else {
//                PartialOrder partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), Integer.toString(validatedOrder.getQuantity()), validatedOrder.getSide());
//                jedis.lpush("makeOrderExchange1", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(partialOrder)));
//            }
            PartialOrder partialOrder = new PartialOrder(validatedOrder.getOrderId(), validatedOrder.getTicker(), validatedOrder.getPrice(), Integer.toString(validatedOrder.getQuantity()), validatedOrder.getSide());
          jedis.lpush("makeOrderExchange1", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(partialOrder)));
        }
    }
}

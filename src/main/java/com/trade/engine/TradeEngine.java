package com.trade.engine;

import com.trade.models.ValidatedOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;

@SpringBootApplication
public class TradeEngine {
    private static final int PORT = 8090;
    private static Jedis jedis = null;

    public static void main(String[] args) {
        SpringApplication.run(TradeEngine.class, args);

//		todo: Get list of validated orders, published by the order validated.
        try {
//            Jedis object for connecting to the redis-server
            jedis = getConnection(PORT);

//            creating JedisPubSub object for subscribing to channels
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Printing published message.");
                    System.out.println(message);
                    List validatedOrders = (List) ObjectSerializer.unserizlize(Base64.getDecoder().decode(message.getBytes()));

                    assert validatedOrders != null;
                    validatedOrders.forEach(validatedOrder -> {
                        System.out.println(validatedOrder.toString());
                    });

//					push validated orders to the "makeOrder" queue
                    pushDataToQueue(validatedOrders);
                }

                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    super.onPMessage(pattern, channel, message);
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


//		MAKE ORDER BOOK REQUEST
        new Thread(new Runnable() {
            final Jedis jedis = new Jedis();
            @Override
            public void run() {
                while (true) {
//					String data = jedis.rpop("makeOrder");

                    List<String> item = jedis.blpop(0, "makeOrder");
                    System.out.println("Key: " + item.get(0));
                    System.out.println("Value: " + item.get(1));
                    byte[] data = Base64.getDecoder().decode(item.get(1).getBytes());

                    if (data == null) continue;
                    ValidatedOrder validatedOrder = (ValidatedOrder) ObjectSerializer.unserizlize(data);

//					jedis.set(validatedOrder.id,data);
                    jedis.lpush("monitorQueue", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(validatedOrder.getId())));

                    OrderBookRequest orderBookRequest = new OrderBookRequest(
                            validatedOrder.getId(),
                            validatedOrder.getOrderItem().getProduct(),
                            validatedOrder.getOrderItem().getSide()
                    );

                    String requestString = Utility.convertToString(orderBookRequest);
                    jedis.lpush("exchange1-orderRequest", requestString);
                    jedis.lpush("exchange2-orderRequest", requestString);
                }
            }
        }).start();

//
//
//
//
//
//		//	MONITOR QUEUE
//		new Thread(new Runnable() {
//			Jedis jedis = new Jedis();
//			@Override
//			public void run() {
////					String data = jedis.rpop("monitorQueue");
////                System.out.println("Data: " + data);
////                String validatedOrderId = (String) ObjectSerializer.unserizlize(Base64.getDecoder().decode(data));
////                System.out.println("Validated order: " + validatedOrderId);
//
//				while (true){
//					String data = jedis.rpop("monitorQueue");
//					if(data == null) continue;
//					String validatedOrderId = (String) ObjectSerializer.unserizlize(Base64.getDecoder().decode(data));
//
//
//					if(jedis.llen(data+"orderbook") == 2){
//                    // TODO: 11/20/20 Serialise objects using ObjectSerializer class,
//                    //  instead of the Utility.convertToObject methods, and remove the Utility Class.
//						PendingOrder[] pendingOrderList1 = Utility.convertToObject(jedis.rpop(data+"orderbook"),PendingOrder[].class);
//						PendingOrder[] pendingOrderList2 = Utility.convertToObject(jedis.rpop(data+"orderbook"),PendingOrder[].class);
//						/*
//						 * Logic
//						 * Get Exchange orders into one list
//						 * Sort list by price
//						 * if (side==Buy){
//						 * sort  Asc
//						 * else
//						 * sort desc
//						 * Start popping from top till order quantity is fulfilled
//						 * }
//						 *  */
//
//						//Exchange Orders into one list
//						List<PendingOrder> pendingOrderList= new ArrayList(Arrays.asList(pendingOrderList1,pendingOrderList2));
//
//						//sorting
//						if (validatedOrder.side.toLowerCase() == "buy") {
//							pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice));
//						} else {
//							pendingOrderList.sort(Comparator.comparing(PendingOrder::getPrice).reversed());
//						}
//
//						//Popping and fulfilling orders
//						int orderquantity =0;
//						PendingOrder curExchangeOrder;
//						int availableQty, buffer = 0;
//						ValidatedOrder partialOrder = null;
//						int targetQty =Integer.valueOf( validatedOrder.quantity);
//
////						while (orderquantity<=targetQty){
////
////							curExchangeOrder = pendingOrderList.remove(0);
////							availableQty=curExchangeOrder.quantity-curExchangeOrder.cumulativeQuantity;
////
////							if (availableQty >= targetQty) {
////								partialOrder= new ValidatedOrder(validatedOrder.id,validatedOrder.product, validatedOrder.price,String.valueOf(targetQty),validatedOrder.side );
////								orderquantity+=Integer.valueOf(availableQty);
////								jedis.lpush("makeOrder"+curExchangeOrder.exchange,partialOrder.toString());
////							}
////							else if (availableQty < targetQty ) {
////								orderquantity+=availableQty;
////								buffer=  orderquantity>targetQty ?
////										targetQty-orderquantity-availableQty
////										: availableQty;
////								 partialOrder= new ValidatedOrder(validatedOrder.id,validatedOrder.product, validatedOrder.price,String.valueOf(buffer),validatedOrder.side );
////								jedis.lpush("makeOrder"+curExchangeOrder.exchange,partialOrder.toString());
////								orderquantity+=availableQty;
////							}
//
//						while (targetQty>0){
//							curExchangeOrder = pendingOrderList.remove(0);
//							availableQty= curExchangeOrder.quantity- curExchangeOrder.cumulativeQuantity;
//							if (availableQty >= targetQty) {
//								partialOrder=new ValidatedOrder(validatedOrder.id,validatedOrder.product, validatedOrder.price,String.valueOf(targetQty),validatedOrder.side );
//								targetQty=0;
//								jedis.lpush("makeOrder"+curExchangeOrder.exchange,partialOrder.toString());
//							}
//							else if(pendingOrderList.isEmpty()){
//								partialOrder=new ValidatedOrder(validatedOrder.id,validatedOrder.product, validatedOrder.price,String.valueOf(targetQty),validatedOrder.side );
//								jedis.lpush("monitorQueue",partialOrder.toString());
//							}
//							else{
//								partialOrder=new ValidatedOrder(validatedOrder.id,validatedOrder.product, validatedOrder.price,String.valueOf(availableQty),validatedOrder.side );
//								targetQty-=availableQty;
//								jedis.lpush("makeOrder"+curExchangeOrder.exchange,partialOrder.toString());
//							}
//
//						}
//
//					}else{
//						jedis.lpush("monitorQueue",data);
//					}
//				}
//			}
//		}).start();
//

    }

    static void pushDataToQueue(List order) {
        Jedis jedis = null;
        try {
            jedis = getConnection(9090);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        jedis.rpush("makeOrder", Base64.getEncoder().encodeToString(ObjectSerializer.serialize(order)));
        System.out.println("List pushed to makeOrder queue");
    }

    private static Jedis getConnection(int port) throws URISyntaxException {
        URI redisURI = null;
        if(System.getenv("REDIS_URL") != null){
            redisURI = new URI(System.getenv("REDIS_URL"));
        }else{
            redisURI = new URI("localhost:" + port);
        }
        return (new Jedis(redisURI));
    }

}




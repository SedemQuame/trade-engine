package com.trade;

import com.trade.models.*;
import com.trade.service.PubSub;
import com.trade.utility.ObjectSerializer;
import com.trade.utility.Utility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@SpringBootApplication
public class TradeEngine {
    private static final int PORT = 8090;
    private static Jedis jedis = null;

//    6379

    public static void main(String[] args) {
        SpringApplication.run(TradeEngine.class, args);

//        PUBSUB
        Thread pubsub = new Thread(new PubSub());
        pubsub.start();

//		MAKE ORDER BOOK REQUEST
        Thread makeOrder = new Thread(new MakeOrder());
        makeOrder.start();

//        	MONITOR QUEUE
        Thread monitorQueue = new Thread(new MonitorQueue());
        monitorQueue.start();

    }



    private static Jedis getConnection() throws URISyntaxException {
        URI redisURI = null;
        if (System.getenv("REDIS_URL") != null) {
            redisURI = new URI(System.getenv("REDIS_URL"));
        } else {
            redisURI = new URI("http://localhost:" + PORT);
        }
        return (new Jedis(redisURI));
    }

}




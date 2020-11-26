package com.trade;

import com.trade.models.MakeOrder;
import com.trade.models.MonitorQueue;
import com.trade.service.PubSub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradeEngine {
    public static void main(String[] args) {
        SpringApplication.run(TradeEngine.class, args);

//        PUBSUB
        Thread pubsub = new Thread(new PubSub());
        pubsub.start();

//		MAKE ORDER BOOK REQUEST
        Thread makeOrder = new Thread(new MakeOrder());
        makeOrder.start();

//      MONITOR QUEUE
        Thread monitorQueue = new Thread(new MonitorQueue());
        monitorQueue.start();
    }
}




package com.trade.models;

import java.io.Serializable;

public class OrderBookRequest implements Serializable {
    private static final long serialVersionUID = 2228145111069333864L;
    public String id;
    public String product;
    public String side;

    public OrderBookRequest() {
    }

    public OrderBookRequest(String id, String product, String side) {
        this.id = id;
        this.product = product;
        this.side = side;
    }

    @Override
    public String toString() {
        return "OrderBookRequest{" +
                "id='" + id + '\'' +
                ", product='" + product + '\'' +
                ", side='" + side + '\'' +
                '}';
    }
}

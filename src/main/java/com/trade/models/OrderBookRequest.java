package com.trade.models;

public class OrderBookRequest {
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

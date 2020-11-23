package com.trade.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public @Data class PartialOrder {
    private @Getter @Setter String orderId;
    private @Getter @Setter String ticker;
    private @Getter @Setter Double price;
    private @Getter @Setter String quantity;
    private @Getter @Setter String side;

    public PartialOrder(String orderId, String ticker, Double price, String quantity, String side) {
        this.orderId = orderId;
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}

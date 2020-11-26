package com.trade.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

public @Data class PartialOrder implements Serializable {
    private static final long serialVersionUID = 2228145111069333864L;
    private @Getter @Setter String orderId;
    private @Getter @Setter String product;
    private @Getter @Setter Double price;
    private @Getter @Setter String quantity;
    private @Getter @Setter String side;

    public PartialOrder(String orderId, String product, Double price, String quantity, String side) {
        this.orderId = orderId;
        this.product = product;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    @Override
    public String toString() {
        return "PartialOrder{" +
                "orderId='" + orderId + '\'' +
                ", ticker='" + product + '\'' +
                ", price=" + price +
                ", quantity='" + quantity + '\'' +
                ", side='" + side + '\'' +
                '}';
    }
}

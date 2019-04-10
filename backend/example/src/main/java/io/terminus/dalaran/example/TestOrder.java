package io.terminus.dalaran.example;

import java.io.Serializable;

public class TestOrder implements Serializable {

    private String orderNumber;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}

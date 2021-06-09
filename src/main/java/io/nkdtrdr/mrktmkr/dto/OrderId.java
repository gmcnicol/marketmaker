package io.nkdtrdr.mrktmkr.dto;

import java.io.Serializable;


public class OrderId implements Serializable {

    private String orderId;
    private Order.TriggerDirection triggerDirection;

    public OrderId() {
    }

    public OrderId(final String orderId, final Order.TriggerDirection triggerDirection) {
        this.orderId = orderId;
        this.triggerDirection = triggerDirection;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public Order.TriggerDirection getTriggerDirection() {
        return triggerDirection;
    }

    public void setTriggerDirection(final Order.TriggerDirection triggerDirection) {
        this.triggerDirection = triggerDirection;
    }
}

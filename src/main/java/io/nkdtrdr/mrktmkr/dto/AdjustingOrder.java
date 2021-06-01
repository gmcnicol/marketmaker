package io.nkdtrdr.mrktmkr.dto;

import io.nkdtrdr.mrktmkr.utilities.OrderCalculations;

import java.math.BigDecimal;

public abstract class AdjustingOrder {
    private final Order innerOrder;
    private final OrderCalculations orderCalculations;

    public AdjustingOrder(Order innerOrder, OrderCalculations orderCalculations) {
        this.innerOrder = innerOrder;
        this.orderCalculations = orderCalculations;
    }

    protected Order getInnerOrder() {
        return innerOrder;
    }


    public Order.OrderSide getSide() {
        return innerOrder.getSide();
    }

    public void setSide(Order.OrderSide side) {
        innerOrder.setSide(side);
    }

    public String getSymbol() {
        return innerOrder.getSymbol();
    }

    public void setSymbol(String symbol) {
        innerOrder.setSymbol(symbol);
    }

    public BigDecimal getQuantity() {
        return innerOrder.getQuantity();
    }

    public void setQuantity(BigDecimal quantity) {
        innerOrder.setQuantity(quantity);
    }

    public BigDecimal getPrice() {
        return innerOrder.getPrice();
    }

    public void setPrice(BigDecimal price) {
        innerOrder.setPrice(price);
    }

    public Order.TimeInForce getTimeInForce() {
        return innerOrder.getTimeInForce();
    }

    public void setTimeInForce(Order.TimeInForce timeInForce) {
        innerOrder.setTimeInForce(timeInForce);
    }

    public String getOrderId() {
        return innerOrder.getOrderId();
    }

    public void setOrderId(String orderId) {
        innerOrder.setOrderId(orderId);
    }

    public String getStrategy() {
        return innerOrder.getStrategy();
    }

    public void setStrategy(String strategy) {
        innerOrder.setStrategy(strategy);
    }

    public Order.OrderTrigger getOrderTrigger() {
        return innerOrder.getOrderTrigger();
    }

    public void setOrderTrigger(Order.OrderTrigger orderTrigger) {
        innerOrder.setOrderTrigger(orderTrigger);
    }

    public BigDecimal getValue() {
        return innerOrder.getValue();
    }

    public void setValue(BigDecimal value) {
        innerOrder.setValue(value);
    }
}

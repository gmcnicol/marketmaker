package io.nkdtrdr.mrktmkr.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

public class TradeUpdateEvent {
    private String eventType;
    private String newClientOrderId;
    private Order.OrderSide orderSide;
    private BigDecimal price;
    private ExecutionType executionType;
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getNewClientOrderId() {
        return newClientOrderId;
    }

    public void setNewClientOrderId(final String newClientOrderId) {
        this.newClientOrderId = newClientOrderId;
    }

    public Order.OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(final Order.OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(final ExecutionType executionType) {
        this.executionType = executionType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("executionType", executionType)
                .append("newClientOrderId", newClientOrderId)
                .append("eventType", eventType)
                .append("orderSide", orderSide)
                .append("symbol", symbol)
                .append("price", price)
                .toString();
    }
}

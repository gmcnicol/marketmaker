package io.nkdtrdr.mrktmkr.dto;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;


@Entity(name = "trade")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal value;
    private OrderSide side;
    private TimeInForce timeInForce;
    private String orderId;
    private String strategy;
    private OrderTrigger orderTrigger;
    private TriggerDirection triggerDirection;
    private String orderStatus;
    private Boolean wasTraded = false;

    public Order() {

    }

    private Order(final Builder builder) {
        setId(builder.id);
        setSymbol(builder.symbol);
        setQuantity(builder.quantity);
        setPrice(builder.price);
        setValue(builder.value);
        setSide(builder.side);
        setTimeInForce(builder.timeInForce);
        setOrderId(builder.orderId);
        setStrategy(builder.strategy);
        setOrderTrigger(builder.orderTrigger);
        setTriggerDirection(builder.triggerDirection);
        setOrderStatus(builder.orderStatus);
    }

    public static boolean orderIsABuy(Order order) {
        return OrderSide.BUY.equals(order.getSide());
    }

    public static boolean orderIsASell(Order order) {
        return OrderSide.SELL.equals(order.getSide());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final Order copy) {
        Builder builder = new Builder();
        builder.id = copy.getId();
        builder.symbol = copy.getSymbol();
        builder.quantity = copy.getQuantity();
        builder.price = copy.getPrice();
        builder.value = copy.getValue();
        builder.side = copy.getSide();
        builder.timeInForce = copy.getTimeInForce();
        builder.orderId = copy.getOrderId();
        builder.strategy = copy.getStrategy();
        builder.orderTrigger = copy.getOrderTrigger();
        builder.triggerDirection = copy.getTriggerDirection();
        builder.orderStatus = copy.getOrderStatus();
        return builder;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getValue() {
        value = getPrice().multiply(getQuantity());
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(final OrderSide side) {
        this.side = side;
    }

    public TimeInForce getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(final TimeInForce timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(final String strategy) {
        this.strategy = strategy;
    }

    public OrderTrigger getOrderTrigger() {
        return orderTrigger;
    }

    public void setOrderTrigger(final OrderTrigger orderTrigger) {
        this.orderTrigger = orderTrigger;
    }

    public TriggerDirection getTriggerDirection() {
        return triggerDirection;
    }

    public void setTriggerDirection(final TriggerDirection triggerDirection) {
        this.triggerDirection = triggerDirection;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Boolean getWasTraded() {
        return wasTraded;
    }

    public void setWasTraded(final Boolean wasTraded) {
        this.wasTraded = wasTraded;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(symbol)
                .addValue(orderId)
                .addValue(side)
                .addValue(quantity)
                .addValue(price)
                .add("value", getValue())
                .add("triggerDirection", triggerDirection)
                .toString();
    }

    /**
     * {@code Order} builder static inner class.
     */
    public enum OrderSide {BUY, SELL}

    public enum OrderTrigger {
        IMMEDIATE,
        PRICE
    }

    public enum TimeInForce {
        GTC, IOC, FOK
    }

    public enum TriggerDirection {
        INTENDED,
        BAIL_OUT,
        NA
    }

    /**
     * {@code Order} builder static inner class.
     */
    public static final class Builder {
        private Long id;
        private String symbol;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal value;
        private OrderSide side;
        private TimeInForce timeInForce;
        private String orderId;
        private String strategy;
        private OrderTrigger orderTrigger;
        private TriggerDirection triggerDirection;
        private String orderStatus;

        private Builder() {
        }

        /**
         * Sets the {@code id} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param id the {@code id} to set
         * @return a reference to this Builder
         */
        public Builder setId(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the {@code symbol} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param symbol the {@code symbol} to set
         * @return a reference to this Builder
         */
        public Builder setSymbol(final String symbol) {
            this.symbol = symbol;
            return this;
        }

        /**
         * Sets the {@code quantity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param quantity the {@code quantity} to set
         * @return a reference to this Builder
         */
        public Builder setQuantity(final BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the {@code price} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param price the {@code price} to set
         * @return a reference to this Builder
         */
        public Builder setPrice(final BigDecimal price) {
            this.price = price;
            return this;
        }

        /**
         * Sets the {@code value} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param value the {@code value} to set
         * @return a reference to this Builder
         */
        public Builder setValue(final BigDecimal value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the {@code side} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param side the {@code side} to set
         * @return a reference to this Builder
         */
        public Builder setSide(final OrderSide side) {
            this.side = side;
            return this;
        }

        /**
         * Sets the {@code timeInForce} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param timeInForce the {@code timeInForce} to set
         * @return a reference to this Builder
         */
        public Builder setTimeInForce(final TimeInForce timeInForce) {
            this.timeInForce = timeInForce;
            return this;
        }

        /**
         * Sets the {@code orderId} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param orderId the {@code orderId} to set
         * @return a reference to this Builder
         */
        public Builder setOrderId(final String orderId) {
            this.orderId = orderId;
            return this;
        }

        /**
         * Sets the {@code strategy} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param strategy the {@code strategy} to set
         * @return a reference to this Builder
         */
        public Builder setStrategy(final String strategy) {
            this.strategy = strategy;
            return this;
        }

        /**
         * Sets the {@code orderTrigger} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param orderTrigger the {@code orderTrigger} to set
         * @return a reference to this Builder
         */
        public Builder setOrderTrigger(final OrderTrigger orderTrigger) {
            this.orderTrigger = orderTrigger;
            return this;
        }

        /**
         * Sets the {@code triggerDirection} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param triggerDirection the {@code triggerDirection} to set
         * @return a reference to this Builder
         */
        public Builder setTriggerDirection(final TriggerDirection triggerDirection) {
            this.triggerDirection = triggerDirection;
            return this;
        }

        /**
         * Sets the {@code orderStatus} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param orderStatus the {@code orderStatus} to set
         * @return a reference to this Builder
         */
        public Builder setOrderStatus(final String orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        /**
         * Returns a {@code Order} built from the parameters previously set.
         *
         * @return a {@code Order} built with parameters of this {@code Order.Builder}
         */
        public Order build() {
            return new Order(this);
        }
    }
}

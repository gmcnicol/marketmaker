package io.nkdtrdr.mrktmkr.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;

import static io.nkdtrdr.mrktmkr.dto.Order.OrderSide.BUY;
import static io.nkdtrdr.mrktmkr.dto.Order.OrderSide.SELL;

@RedisHash("order")
public class Order {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal value;
    private OrderSide side;
    private TimeInForce timeInForce;
    @Id
    private String orderId;
    private String strategy;
    private OrderTrigger orderTrigger;
    private TriggerDirection triggerDirection;

    public Order() {
    }

    public Order(final String symbol, final BigDecimal quantity, final BigDecimal price, final BigDecimal value,
                 final OrderSide side, final TimeInForce timeInForce, final String orderId, final String strategy,
                 final OrderTrigger orderTrigger, final TriggerDirection triggerDirection) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.value = value;
        this.side = side;
        this.timeInForce = timeInForce;
        this.orderId = orderId;
        this.strategy = strategy;
        this.orderTrigger = orderTrigger;
        this.triggerDirection = triggerDirection;
    }

    private Order(final Builder builder) {
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
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final Order copy) {
        Builder builder = new Builder();
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
        return builder;
    }

    public static boolean orderIsASell(final Order order) {
        return SELL.equals(order.getSide());
    }

    public static boolean orderIsABuy(final Order order) {
        return BUY.equals(order.getSide());
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
        this.value = null;
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.value = null;
        this.price = price;
    }

    public BigDecimal getValue() {
        if (value != null) {
            return value;
        }
        value = this.getQuantity().multiply(this.getPrice());
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", orderId)
                .addValue(triggerDirection)
                .addValue(strategy)
                .addValue(orderTrigger)

                .add("quantity", quantity)
                .add("price", price)
                .add("value", value)
                .add("side", side)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equal(getSymbol(),
                order.getSymbol()) && Objects.equal(getQuantity(), order.getQuantity()) && Objects.equal(getPrice(),
                order.getPrice()) && getSide() == order.getSide() && getTimeInForce() == order.getTimeInForce() && Objects.equal(getOrderId(), order.getOrderId()) && Objects.equal(getStrategy(), order.getStrategy()) && getOrderTrigger() == order.getOrderTrigger() && getTriggerDirection() == order.getTriggerDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSymbol(), getQuantity(), getPrice(), getSide(), getTimeInForce(), getOrderId(),
                getStrategy(), getOrderTrigger(), getTriggerDirection());
    }

    public TriggerDirection getTriggerDirection() {
        return triggerDirection;
    }

    public void setTriggerDirection(final TriggerDirection triggerDirection) {
        this.triggerDirection = triggerDirection;
    }

    public enum OrderSide {BUY, SELL}

    public enum OrderTrigger {
        IMMEDIATE,
        PRICE
    }

    public enum TimeInForce {
        GTC, IOC, FOK
    }

    public enum TriggerDirection {
        FROM_ABOVE,
        FROM_BELOW,
        NA
    }

    /**
     * {@code Order} builder static inner class.
     */
    public static final class Builder {
        private String symbol;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal value;
        private OrderSide side;
        private TimeInForce timeInForce = TimeInForce.GTC;
        private String orderId;
        private String strategy;
        private OrderTrigger orderTrigger;
        private TriggerDirection triggerDirection = TriggerDirection.NA;

        private Builder() {
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
         * Returns a {@code Order} built from the parameters previously set.
         *
         * @return a {@code Order} built with parameters of this {@code Order.Builder}
         */
        public Order build() {
            return new Order(this);
        }
    }
}

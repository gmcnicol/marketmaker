package io.nkdtrdr.mrktmkr.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;


/**
 * The type Order calculations.
 */
public class OrderCalculations {
    public static final int DEFAULT_SCALE = 8;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal cost;
    private RoundingMode rounding;
    private Function<BigDecimal, BigDecimal> modifier = Function.identity();

    public static OrderCalculations aCalculator() {
        return new OrderCalculations();
    }

    /**
     * With rounding order calculations.
     *
     * @param rounding the rounding
     * @return the order calculations
     */
    public OrderCalculations withRounding(RoundingMode rounding) {
        this.rounding = rounding;
        return this;
    }

    /**
     * With quantity order calculations.
     *
     * @param quantity the quantity
     * @return the order calculations
     */
    public OrderCalculations withQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * With price order calculations.
     *
     * @param price the price
     * @return the order calculations
     */
    public OrderCalculations withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    /**
     * With cost order calculations.
     *
     * @param cost the cost
     * @return the order calculations
     */
    public OrderCalculations withCost(BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    public OrderCalculations withModifier(Function<BigDecimal, BigDecimal> modifier) {
        this.modifier = modifier;
        return this;
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity(int scale) {
        if (quantity != null) {
            return quantity;
        }
        if (cost == null || ((price == null) || (price.compareTo(ZERO) == 0))) return ZERO;
        final BigDecimal q = cost.divide(price, scale, rounding);
        return modifier.apply(q);
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public BigDecimal getPrice(int scale) {
        if (price != null) {
            return price;
        }
        return (cost == null || (quantity == null || quantity.compareTo(ZERO) == 0))
                ? ZERO
                : modifier.apply(cost.divide(quantity, scale, rounding));
    }

    /**
     * Gets cost.
     *
     * @return the cost
     */
    public BigDecimal getCost(int scale) {
        if (cost != null) {
            return cost;
        }
        return (price == null || quantity == null)
                ? ZERO
                : modifier.apply(price.multiply(quantity)).setScale(scale, rounding);
    }

    @Override
    public String toString() {
        return "OrderCalculations{" +
                "quantity=" + getQuantity(DEFAULT_SCALE) +
                ", price=" + getPrice(DEFAULT_SCALE) +
                ", cost=" + getCost(DEFAULT_SCALE) +
                ", rounding=" + rounding +
                '}';
    }
}

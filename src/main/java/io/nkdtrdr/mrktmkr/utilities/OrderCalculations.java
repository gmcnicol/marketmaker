package io.nkdtrdr.mrktmkr.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

import static java.math.BigDecimal.ZERO;


/**
 * The type Order calculations.
 */
public class OrderCalculations {
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
    public BigDecimal getPrice() {
        if (price != null) {
            return price;
        }
        return (cost == null || (quantity == null || quantity.compareTo(ZERO) == 0)) ? ZERO :
                modifier.apply(cost.divide(quantity, 2,
                        rounding));
    }

    /**
     * Gets cost.
     *
     * @return the cost
     */
    public BigDecimal getCost() {
        if (cost != null) {
            return cost;
        }
        return (price == null || quantity == null) ? ZERO : modifier.apply(price.multiply(quantity)).setScale(2,
                rounding);
    }

    @Override
    public String toString() {
        return "OrderCalculations{" +
                "quantity=" + getQuantity(8) +
                ", price=" + getPrice() +
                ", cost=" + getCost() +
                ", rounding=" + rounding +
                '}';
    }
}

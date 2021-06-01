package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.dto.Order;

import java.util.Collection;

/**
 * Trading strategy
 *
 * @param <T> the analysis type parameter
 */
public interface TradingStrategy<T> {
    /**
     * Process latest reading.
     *
     * @param value the value
     */
    void processLatestReading(final T value);

    /**
     * Can be activated boolean.
     *
     * @return the boolean
     */
    boolean canBeActivated();

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Can place order boolean.
     *
     * @param order the order
     * @return the boolean
     */
    boolean canPlaceOrder(final Order order);

    /**
     * Gets initial order.
     *
     * @return the initial order
     */
    Order getInitialOrder();

    boolean canTrade();

    void setMediator(StrategyMediator mediator);
}

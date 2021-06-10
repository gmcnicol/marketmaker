package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.dto.Order;


/**
 * The type Kd forwarding trading strategy.
 */
public class KdForwardingTradingStrategy implements KdTradingStrategy {

    private KdTradingStrategy innerStrategy;

    /**
     * Sets inner strategy.
     *
     * @param innerStrategy the inner strategy
     */
    public void setInnerStrategy(final KdTradingStrategy innerStrategy) {
        this.innerStrategy = innerStrategy;
    }

    @Override
    public void processLatestReading(final KdValue value) {
        if (null != innerStrategy)
            innerStrategy.processLatestReading(value);
    }

    @Override
    public boolean canBeActivated() {
        return null != innerStrategy && innerStrategy.canBeActivated();
    }

    @Override
    public String getName() {
        return null != innerStrategy ? innerStrategy.getName() : "";
    }

    @Override
    public boolean canPlaceOrder(final Order order) {
        return null != innerStrategy && innerStrategy.canPlaceOrder(order);
    }

    @Override
    public Order getInitialOrder() {
        return innerStrategy.getInitialOrder();
    }

    @Override
    public boolean canTrade() {
        return null != innerStrategy && innerStrategy.canTrade();
    }

    @Override
    public void setMediator(StrategyMediator mediator) {

    }

    @Override
    public boolean isLocked() {
        return innerStrategy.isLocked();
    }

    @Override
    public void setLocked(final boolean locked) {
        innerStrategy.setLocked(locked);
    }
}

package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;


@Component
public class TriggersFacade {
    private final TriggersMediator triggersMediator;
    private final StrategyFacade strategyFacade;
    public TriggersFacade(TriggersMediator triggersMediator, final StrategyFacade strategyFacade) {
        this.triggersMediator = triggersMediator;
        this.strategyFacade = strategyFacade;
    }

    public void setLocked(final boolean locked) {
        strategyFacade.setLocked(locked);
    }

    public void createTriggersForOrder(Order order, Consumer<EventEnvelope> callback) {
        final Collection<Order> orders = triggersMediator.getTriggersForOrder(order);
        orders.stream()
                .map(OrdersFacade::eventEnvelopFromOrder)
                .forEach(callback);
    }
}

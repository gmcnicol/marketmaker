package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;


@Component
public class TriggersFacade {
    private final TriggersMediator triggersMediator;

    public TriggersFacade(TriggersMediator triggersMediator) {
        this.triggersMediator = triggersMediator;
    }

    public void createTriggersForOrder(Order order, Consumer<EventEnvelope> callback) {
        final Collection<Order> orders = triggersMediator.getTriggersForOrder(order);
        orders.stream()
                .map(OrdersFacade::eventEnvelopFromOrder)
                .forEach(callback);
    }
}

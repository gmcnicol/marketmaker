package io.nkdtrdr.mrktmkr.orders.processors;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PriceTriggerOrderRequestedEventProcessor implements EventProcessor {
    private final OrdersFacade ordersFacade;

    public PriceTriggerOrderRequestedEventProcessor(final OrdersFacade ordersFacade) {
        this.ordersFacade = ordersFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "ORDER_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final Order order = (Order) makerEvent.getEventEnvelope().getPayload();

        if (order.getOrderTrigger().equals(Order.OrderTrigger.PRICE))
            ordersFacade.addOrderToBeTriggered(order);
    }

}

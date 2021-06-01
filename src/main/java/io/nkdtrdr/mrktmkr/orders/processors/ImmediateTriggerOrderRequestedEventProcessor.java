package io.nkdtrdr.mrktmkr.orders.processors;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ImmediateTriggerOrderRequestedEventProcessor implements EventProcessor {
    private final OrdersFacade ordersFacade;
    private final ProcessMediator processMediator;

    public ImmediateTriggerOrderRequestedEventProcessor(final OrdersFacade ordersFacade,
                                                        final ProcessMediator processMediator) {
        this.ordersFacade = ordersFacade;
        this.processMediator = processMediator;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "ORDER_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final Order order = (Order) makerEvent.getEventEnvelope().getPayload();
        if (!ordersFacade.isOrderIdOpen(order.getOrderId())
                && order.getOrderTrigger().equals(Order.OrderTrigger.IMMEDIATE)
        ) {
            if (ordersFacade.canPlaceOrder(order)) {
                processMediator.placeOrder(order);
                ordersFacade.trackOpenOrder(order);
            }
        }
    }
}

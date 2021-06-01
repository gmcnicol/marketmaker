package io.nkdtrdr.mrktmkr.persistence.processors;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.persistence.PersistenceFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class PersistenceOrderRequestedHandler implements EventProcessor {
    private final PersistenceFacade persistenceFacade;

    public PersistenceOrderRequestedHandler(final PersistenceFacade persistenceFacade) {
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "ORDER_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final Order order = (Order) makerEvent.getEventEnvelope().getPayload();
        if (order.getOrderTrigger().equals(Order.OrderTrigger.PRICE)) {
            persistenceFacade.trackOrder(order);
        }
    }
}

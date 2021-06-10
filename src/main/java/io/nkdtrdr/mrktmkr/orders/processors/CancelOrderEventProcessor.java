package io.nkdtrdr.mrktmkr.orders.processors;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CancelOrderEventProcessor implements EventProcessor {
    private final ProcessMediator processMediator;
    private final OrdersFacade ordersFacade;

    public CancelOrderEventProcessor(final ProcessMediator processMediator, final OrdersFacade ordersFacade) {
        this.processMediator = processMediator;
        this.ordersFacade = ordersFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "CANCEL_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final String orderId = makerEvent.getEventEnvelope().getPayload().toString();
        processMediator.cancelOrder(orderId, "BTCGBP");
        ordersFacade.stopTrackingOrderId(orderId);

    }
}

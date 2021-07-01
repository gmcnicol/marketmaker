package io.nkdtrdr.mrktmkr.orders.processors;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class CancelOrderEventProcessor implements EventProcessor {
    private final ProcessMediator processMediator;
    private final OrdersFacade ordersFacade;
    private final Symbol symbol;

    public CancelOrderEventProcessor(final ProcessMediator processMediator, final OrdersFacade ordersFacade,
                                     final Symbol symbol) {
        this.processMediator = processMediator;
        this.ordersFacade = ordersFacade;
        this.symbol = symbol;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "CANCEL_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final String orderId = makerEvent.getEventEnvelope().getPayload().toString();
        processMediator.cancelOrder(orderId, symbol.getSymbol());
        ordersFacade.stopTrackingOrderId(orderId);
    }
}

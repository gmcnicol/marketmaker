package io.nkdtrdr.mrktmkr.orders.processors;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class TriggeredOrderCompleteEventProcessor implements EventProcessor {
    private final OrdersFacade ordersFacade;

    public TriggeredOrderCompleteEventProcessor(final OrdersFacade ordersFacade) {
        this.ordersFacade = ordersFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "USER_TRADE_UPDATED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final OrderTradeUpdateEvent event = (OrderTradeUpdateEvent) makerEvent.getEventEnvelope().getPayload();
        if (event.getOrderStatus().equals(OrderStatus.FILLED)) {
            ordersFacade.removeTrigger(event.getNewClientOrderId());
            ordersFacade.stopTrackingOrderId(event.getNewClientOrderId());
        }
    }
}

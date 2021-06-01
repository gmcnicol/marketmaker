package io.nkdtrdr.mrktmkr.persistence.processors;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.persistence.PersistenceFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class PersistedOrderFilledProcessor implements EventProcessor {
    private final PersistenceFacade facade;

    public PersistedOrderFilledProcessor(final PersistenceFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "USER_TRADE_UPDATED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final OrderTradeUpdateEvent event = (OrderTradeUpdateEvent) makerEvent.getEventEnvelope().getPayload();
        if (event.getOrderStatus().equals(OrderStatus.FILLED)) {
            facade.stopTrackingOrderId(event.getNewClientOrderId());
        }
    }
}

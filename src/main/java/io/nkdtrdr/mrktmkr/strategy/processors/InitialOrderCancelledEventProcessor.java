package io.nkdtrdr.mrktmkr.strategy.processors;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class InitialOrderCancelledEventProcessor implements EventProcessor {
    private final StrategyFacade strategyFacade;

    public InitialOrderCancelledEventProcessor(final StrategyFacade strategyFacade) {
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "USER_TRADE_UPDATED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final OrderTradeUpdateEvent orderTradeUpdateEvent =
                (OrderTradeUpdateEvent) makerEvent.getEventEnvelope().getPayload();
        final OrderStatus orderStatus = orderTradeUpdateEvent.getOrderStatus();
        if (orderStatus.equals(OrderStatus.CANCELED))
            strategyFacade.placeInitialOrder();
    }
}

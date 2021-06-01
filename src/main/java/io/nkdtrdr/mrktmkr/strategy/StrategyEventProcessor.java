package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

@Component
public class StrategyEventProcessor implements EventProcessor {
    private static final String STRATEGY_TRIGGERED = "STRATEGY_TRIGGERED";

    private final StrategyMediator mediator;

    public StrategyEventProcessor(final StrategyMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return STRATEGY_TRIGGERED.equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        mediator.setActiveTradingStrategy(makerEvent.getEventEnvelope().getPayload().toString());
    }
}

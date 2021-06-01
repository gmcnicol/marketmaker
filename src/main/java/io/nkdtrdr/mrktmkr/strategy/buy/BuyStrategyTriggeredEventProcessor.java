package io.nkdtrdr.mrktmkr.strategy.buy;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class BuyStrategyTriggeredEventProcessor implements EventProcessor {
    private final StrategyFacade strategyFacade;

    public BuyStrategyTriggeredEventProcessor(final StrategyFacade strategyFacade) {
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "STRATEGY_TRIGGERED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final String strategyName = makerEvent.getEventEnvelope().getPayload().toString();
        if ("BUY".equals(strategyName)) {
            strategyFacade.placeInitialOrder();
        }
    }
}

package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class StrategyEventProcessor implements EventProcessor {
    private static final String STRATEGY_TRIGGERED = "STRATEGY_TRIGGERED";
    private final StrategyFacade strategyFacade;

    public StrategyEventProcessor(final StrategyFacade strategyFacade) {
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return STRATEGY_TRIGGERED.equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final String strategyName = makerEvent.getEventEnvelope().getPayload().toString();
        strategyFacade.setActiveTradingStrategy(strategyName);
        if (strategyFacade.canActivateStrategy(strategyName)) {
            strategyFacade.placeInitialOrder();
        }
    }
}

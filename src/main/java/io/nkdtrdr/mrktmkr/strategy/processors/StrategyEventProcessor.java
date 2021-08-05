package io.nkdtrdr.mrktmkr.strategy.processors;

import io.nkdtrdr.mrktmkr.audit.TradeAudit;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class StrategyEventProcessor implements EventProcessor {
    private static final String STRATEGY_TRIGGERED = "STRATEGY_TRIGGERED";
    private final StrategyFacade strategyFacade;
    private final TradeAudit tradeAudit;

    public StrategyEventProcessor(final StrategyFacade strategyFacade, final TradeAudit tradeAudit) {
        this.strategyFacade = strategyFacade;
        this.tradeAudit = tradeAudit;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return STRATEGY_TRIGGERED.equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final String strategyName = makerEvent.getEventEnvelope().getPayload().toString();
        strategyFacade.setActiveTradingStrategy(strategyName);
        tradeAudit.auditStrategy(strategyName);
        if (strategyFacade.canActivateStrategy(strategyName)) {
            strategyFacade.placeInitialOrder();
        }
    }
}

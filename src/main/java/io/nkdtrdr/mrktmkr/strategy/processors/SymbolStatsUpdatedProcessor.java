package io.nkdtrdr.mrktmkr.strategy.processors;

import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.SymbolStats;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class SymbolStatsUpdatedProcessor implements EventProcessor {
    private final StrategyFacade strategyFacade;

    public SymbolStatsUpdatedProcessor(final StrategyFacade strategyFacade) {
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "SYMBOL_STATS_UPDATED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final SymbolStats payload = (SymbolStats) makerEvent.getEventEnvelope().getPayload();

        strategyFacade.setSymbolStatistics(payload);
    }
}

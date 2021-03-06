package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.dto.SymbolStats;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static io.nkdtrdr.mrktmkr.disruptor.EventEnvelope.EventEnvelopeBuilder.anEventEnvelope;


@Component
public class StrategyFacade {
    private final StrategyMediator strategyMediator;

    public StrategyFacade(final StrategyMediator strategyMediator) {
        this.strategyMediator = strategyMediator;
    }

    public void processKDValue(KdValue kdValue, Consumer<EventEnvelope> callback) {
        strategyMediator.getAllTradingStrategies()
                .stream()
                .peek(s -> s.processLatestReading(kdValue))
                .filter(TradingStrategy::canBeActivated)
                .map(s -> anEventEnvelope()
                        .withEventName("STRATEGY_TRIGGERED")
                        .withPayload(s.getName())
                        .build())
                .forEach(callback);

        if (strategyMediator.currentStrategyCanTrade())
            strategyMediator.placeInitialOrder();
    }

    public void processInitialKDValue(final List<KdValue> kdValues, final Consumer<EventEnvelope> callback) {
        kdValues.forEach(v -> this.processKDValue(v, callback));
    }

    public void placeInitialOrder() {
        strategyMediator.placeInitialOrder();
    }

    public void setActiveTradingStrategy(final String strategyName) {
        strategyMediator.setActiveTradingStrategy(strategyName);
    }

    public boolean canActivateStrategy(final String candidateStrategy) {
        return strategyMediator.canActivateStrategy(candidateStrategy);
    }

    public boolean isLocked(final String strategy) {

        return strategy != null && strategyMediator.isLocked(strategy);
    }

    public void setLocked(final boolean locked) {
        strategyMediator.setLocked(locked);
    }

    public void setSymbolStatistics(final SymbolStats stats) {
        strategyMediator.setSymbolStatistics(stats);
    }
}

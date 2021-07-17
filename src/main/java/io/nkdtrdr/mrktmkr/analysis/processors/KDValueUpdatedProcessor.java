package io.nkdtrdr.mrktmkr.analysis.processors;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class KDValueUpdatedProcessor implements EventProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KDValueUpdatedProcessor.class);
    private final StrategyFacade strategyFacade;

    public KDValueUpdatedProcessor(StrategyFacade strategyFacade) {
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "KD_VALUE_UPDATED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        strategyFacade.processKDValue((KdValue) makerEvent.getEventEnvelope().getPayload(), resultHandler);
    }
}

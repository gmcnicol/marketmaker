package io.nkdtrdr.mrktmkr.strategy;

import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;


@Component
@SuppressWarnings("all")
public class KdValuesInitialisedProcessor implements EventProcessor {
    private final StrategyFacade facade;

    public KdValuesInitialisedProcessor(final StrategyFacade facade) {
        this.facade = facade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "KD_VALUES_INITIALISED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final List<KdValue> payload = (List<KdValue>) makerEvent.getEventEnvelope().getPayload();
        facade.processInitialKDValue(payload, resultHandler);
    }
}

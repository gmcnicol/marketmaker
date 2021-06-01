package io.nkdtrdr.mrktmkr.analysis.processors;

import io.nkdtrdr.mrktmkr.analysis.AnalysisMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.CandleStickInitialisedEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class CandleStickInitialisedProcessor implements EventProcessor {
    private final AnalysisMediator mediator;

    public CandleStickInitialisedProcessor(AnalysisMediator analysisMediator) {
        this.mediator = analysisMediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "CANDLE_STICK_INITIALISED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final CandleStickInitialisedEvent candleStickInitialisedEvent =
                (CandleStickInitialisedEvent) makerEvent.getEventEnvelope().getPayload();
        mediator.initialiseCandleSticksForSymbol(candleStickInitialisedEvent, resultHandler);
    }
}

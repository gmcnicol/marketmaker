package io.nkdtrdr.mrktmkr.analysis;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.dto.CandleStickDTO;
import io.nkdtrdr.mrktmkr.dto.CandleStickInitialisedEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Consumer;

import static io.nkdtrdr.mrktmkr.disruptor.EventEnvelope.EventEnvelopeBuilder.anEventEnvelope;


@Component
public class AnalysisMediator {
    private final ProcessMediator processMediator;
    private final AnalyserManager analyserManager;
    private final CircularFifoQueue<KdValue> lastTwoValues = new CircularFifoQueue<>(2);

    public AnalysisMediator(ProcessMediator processMediator, AnalyserManager analyserManager) {
        this.processMediator = processMediator;
        this.analyserManager = analyserManager;
    }

    public void initialiseCandleSticksForSymbol(CandleStickInitialisedEvent payload,
                                                final Consumer<EventEnvelope> callback) {
        final ArrayList<CandleStickDTO> candlesticks = new ArrayList<>(payload.getCandleStickDTOS());
        final KDAnalyser kdAnalyser = analyserManager.get(payload.getSymbol(), payload.getInterval());
        kdAnalyser.initialise(candlesticks);

        final EventEnvelope eventEnvelope = anEventEnvelope()
                .withPayload(kdAnalyser.getReadings())
                .withEventName("KD_VALUES_INITIALISED").build();

        callback.accept(eventEnvelope);

        processMediator.startCandleStickStream(payload);
    }

    public KdValue updateCandleStick(CandleStickDTO payload) {
        return analyserManager.get(payload.getSymbol(), payload.getIntervalId()).analyse(payload);
    }
}

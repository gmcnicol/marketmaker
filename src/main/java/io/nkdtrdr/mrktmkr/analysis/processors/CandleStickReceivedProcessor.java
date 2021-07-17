package io.nkdtrdr.mrktmkr.analysis.processors;

import io.nkdtrdr.mrktmkr.analysis.AnalysisMediator;
import io.nkdtrdr.mrktmkr.analysis.model.KdValue;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.CandleStickDTO;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static io.nkdtrdr.mrktmkr.disruptor.EventEnvelope.EventEnvelopeBuilder.anEventEnvelope;


@Component
public class CandleStickReceivedProcessor implements EventProcessor {
    private final AnalysisMediator mediator;

    public CandleStickReceivedProcessor(AnalysisMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "CANDLE_STICK_UPDATED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final CandleStickDTO payload = (CandleStickDTO) makerEvent.getEventEnvelope().getPayload();
        if (payload.getBarFinal()) {
            final KdValue kdValue = mediator.updateCandleStick(payload);
            final EventEnvelope.EventEnvelopeBuilder result =
                    anEventEnvelope()
                            .withPayload(kdValue)
                            .withEventName("KD_VALUE_UPDATED");

            resultHandler.accept(result.build());
        }
    }
}

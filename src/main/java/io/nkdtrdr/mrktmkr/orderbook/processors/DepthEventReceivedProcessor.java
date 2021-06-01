package io.nkdtrdr.mrktmkr.orderbook.processors;

import com.binance.api.client.domain.event.DepthEvent;
import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DepthEventReceivedProcessor implements EventProcessor {
    public static final String EVENT_NAME = "DEPTH_EVENT_RECEIVED";
    private final ProcessMediator processMediator;

    public DepthEventReceivedProcessor(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return EVENT_NAME.equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final DepthEvent payload = getPayload(makerEvent);
        processMediator.processDepthEvent(payload.getSymbol(), payload);
    }

    private DepthEvent getPayload(MakerEvent makerEvent) {
        return (DepthEvent) makerEvent.getEventEnvelope().getPayload();
    }
}

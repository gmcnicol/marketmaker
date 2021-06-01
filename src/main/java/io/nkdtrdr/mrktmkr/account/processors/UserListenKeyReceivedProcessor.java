package io.nkdtrdr.mrktmkr.account.processors;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UserListenKeyReceivedProcessor implements EventProcessor {

    private final ProcessMediator processMediator;

    public UserListenKeyReceivedProcessor(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "USER_LISTEN_KEY_RECEIVED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        processMediator.setListenKey(makerEvent.getEventEnvelope().getPayload().toString());
    }
}

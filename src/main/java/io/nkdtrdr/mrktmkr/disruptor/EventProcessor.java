package io.nkdtrdr.mrktmkr.disruptor;

import java.util.function.Consumer;


public interface EventProcessor {
    boolean shouldProcessEventName(String eventName);

    void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler);
}

package io.nkdtrdr.mrktmkr.disruptor.eventhandlers;

import com.lmax.disruptor.EventHandler;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;


public class ProcessorEventHandler implements EventHandler<MakerEvent> {

    private final EventProcessor eventProcessor;

    public ProcessorEventHandler(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void onEvent(MakerEvent makerEvent, long l, boolean b) {
        try {
            if (!eventProcessor.shouldProcessEventName(makerEvent.getEventName()))
                return;
            eventProcessor.process(makerEvent, makerEvent::addResult);
        } catch (Throwable e) {
            makerEvent.setThrowable(e);
        }
    }
}

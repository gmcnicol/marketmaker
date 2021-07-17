package io.nkdtrdr.mrktmkr.disruptor.eventhandlers;

import com.lmax.disruptor.EventHandler;
import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;


@Component
public class PublisherHandler implements EventHandler<MakerEvent> {

    private final ProcessMediator processMediator;

    public PublisherHandler(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    @Override
    public void onEvent(MakerEvent makerEvent, long l, boolean b) throws Exception {
        if (!makerEvent.hasThrown())
            makerEvent.getResults().forEach(this::publishResult);
    }

    private void publishResult(EventEnvelope ee) {
        processMediator.processEvent(ee.getEventName(), ee.getPayload());
    }
}

package io.nkdtrdr.mrktmkr.disruptor.eventhandlers;

import com.lmax.disruptor.EventHandler;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;


public class HasThrownEventHandler implements EventHandler<MakerEvent> {
    private final EventHandler<MakerEvent> innerEventHandler;

    public HasThrownEventHandler(EventHandler<MakerEvent> innerEventHandler) {
        this.innerEventHandler = innerEventHandler;
    }

    @Override
    public void onEvent(MakerEvent makerEvent, long l, boolean b) throws Exception {
        if (!makerEvent.hasThrown()) {
            innerEventHandler.onEvent(makerEvent, l, b);
        }
    }
}

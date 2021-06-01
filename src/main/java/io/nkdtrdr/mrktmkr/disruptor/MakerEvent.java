package io.nkdtrdr.mrktmkr.disruptor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MakerEvent {
    private String eventName;
    private Throwable throwable;
    private EventEnvelope eventEnvelope;
    private final ConcurrentLinkedQueue<EventEnvelope> results = new ConcurrentLinkedQueue<>();

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void clear() {
        eventEnvelope = null;
        eventName = null;
        throwable = null;
        results.clear();
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean hasThrown() {
        return throwable != null;
    }

    public EventEnvelope getEventEnvelope() {
        return eventEnvelope;
    }

    public void setEventEnvelope(EventEnvelope eventEnvelope) {
        this.eventEnvelope = eventEnvelope;
    }

    public void addResult(EventEnvelope eventEnvelope) {
        results.add(eventEnvelope);
    }

    public Queue<EventEnvelope> getResults() {
        return new LinkedList<>(results);
    }
}

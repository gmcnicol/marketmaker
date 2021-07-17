package io.nkdtrdr.mrktmkr.disruptor;

import com.google.common.base.MoreObjects;


public class EventEnvelope {
    private String eventName;
    private Object payload;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("eventName", eventName)
                .add("payload", payload)
                .toString();
    }

    public static final class EventEnvelopeBuilder {
        private String eventName;
        private Object payload;

        private EventEnvelopeBuilder() {
        }

        public static EventEnvelopeBuilder anEventEnvelope() {
            return new EventEnvelopeBuilder();
        }

        public EventEnvelopeBuilder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public EventEnvelopeBuilder withPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        public EventEnvelope build() {
            EventEnvelope eventEnvelope = new EventEnvelope();
            eventEnvelope.setEventName(eventName);
            eventEnvelope.setPayload(payload);
            return eventEnvelope;
        }
    }
}

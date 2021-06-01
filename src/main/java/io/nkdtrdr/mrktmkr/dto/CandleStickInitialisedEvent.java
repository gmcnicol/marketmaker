package io.nkdtrdr.mrktmkr.dto;

import java.util.Collection;

public class CandleStickInitialisedEvent {
    private Collection<CandleStickDTO> candleStickDTOS;
    private String symbol;
    private String interval;

    public Collection<CandleStickDTO> getCandleStickDTOS() {
        return candleStickDTOS;
    }

    public void setCandleStickDTOS(Collection<CandleStickDTO> candleStickDTOS) {
        this.candleStickDTOS = candleStickDTOS;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public static final class CandleStickInitialisedEventBuilder {
        private Collection<CandleStickDTO> candleStickDTOS;
        private String symbol;
        private String interval;

        private CandleStickInitialisedEventBuilder() {
        }

        public static CandleStickInitialisedEventBuilder aCandleStickInitialisedEvent() {
            return new CandleStickInitialisedEventBuilder();
        }

        public CandleStickInitialisedEventBuilder withCandleStickDTOS(Collection<CandleStickDTO> candleStickDTOS) {
            this.candleStickDTOS = candleStickDTOS;
            return this;
        }

        public CandleStickInitialisedEventBuilder withSymbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public CandleStickInitialisedEventBuilder withInterval(String interval) {
            this.interval = interval;
            return this;
        }

        public CandleStickInitialisedEvent build() {
            CandleStickInitialisedEvent candleStickInitialisedEvent = new CandleStickInitialisedEvent();
            candleStickInitialisedEvent.symbol = this.symbol;
            candleStickInitialisedEvent.candleStickDTOS = this.candleStickDTOS;
            candleStickInitialisedEvent.interval = this.interval;
            return candleStickInitialisedEvent;
        }
    }
}

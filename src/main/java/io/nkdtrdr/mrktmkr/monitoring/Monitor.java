package io.nkdtrdr.mrktmkr.monitoring;

import org.springframework.stereotype.Component;


@Component
public class Monitor {
    private long lastTickerUpdate;

    public long getLastTickerUpdate() {
        return lastTickerUpdate;
    }

    public void setLastTickerUpdate(final long lastTickerUpdate) {
        this.lastTickerUpdate = lastTickerUpdate;
    }
}

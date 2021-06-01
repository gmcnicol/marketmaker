package io.nkdtrdr.mrktmkr.analysis.model;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class KdValue {
    private final BigDecimal kValue;
    private final BigDecimal dValue;
    private final long timestamp;
    private final String symbol;
    private final String interval;

    public KdValue(BigDecimal kValue, BigDecimal dValue, String symbol, String interval) {
        this.symbol = symbol;
        this.interval = interval;
        this.timestamp = Instant.now().toEpochMilli();
        this.kValue = kValue.setScale(4, RoundingMode.HALF_UP).movePointRight(2);
        this.dValue = dValue.setScale(4, RoundingMode.HALF_UP).movePointRight(2);
    }

    public BigDecimal getkValue() {
        return kValue;
    }

    public BigDecimal getdValue() {
        return dValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)

                .addValue(symbol)
                .addValue(interval)
                .add("k", kValue)
                .add("d", dValue)
                .toString();
    }
}

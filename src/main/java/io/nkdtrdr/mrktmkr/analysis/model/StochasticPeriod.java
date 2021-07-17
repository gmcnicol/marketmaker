package io.nkdtrdr.mrktmkr.analysis.model;

import io.nkdtrdr.mrktmkr.dto.CandleStickDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class StochasticPeriod {
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int SCALE = 8;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    String symbol;
    String interval;

    public StochasticPeriod(CandleStickDTO candleStickDTO) {
        high = candleStickDTO.getHigh().setScale(SCALE, ROUNDING_MODE);
        low = candleStickDTO.getLow().setScale(SCALE, ROUNDING_MODE);
        close = candleStickDTO.getClose().setScale(SCALE, ROUNDING_MODE);
        symbol = candleStickDTO.getSymbol();
        interval = candleStickDTO.getIntervalId();
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getInterval() {
        return interval;
    }
}

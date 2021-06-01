package io.nkdtrdr.mrktmkr.dto;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;

public class CandleStickDTO {
    private Long openTime;
    private String symbol;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private Long closeTime;
    private String intervalId;
    private Boolean isBarFinal;

    public CandleStickDTO() {
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
    }

    public Boolean getBarFinal() {
        return isBarFinal;
    }

    public void setBarFinal(Boolean barFinal) {
        isBarFinal = barFinal;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("openTime", openTime)
                .add("symbol", symbol)
                .add("open", open)
                .add("high", high)
                .add("low", low)
                .add("close", close)
                .add("volume", volume)
                .add("closeTime", closeTime)
                .add("intervalId", intervalId)
                .add("isBarFinal", isBarFinal)
                .toString();
    }
}

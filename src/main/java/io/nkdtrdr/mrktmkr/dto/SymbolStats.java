package io.nkdtrdr.mrktmkr.dto;

import java.math.BigDecimal;


public class SymbolStats {
    private BigDecimal highPrice;
    private BigDecimal lowPrice;

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(final BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(final BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }
}

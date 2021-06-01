package io.nkdtrdr.mrktmkr.symbols;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.lang.String.format;


@Component
public class Symbol {
    @Value("${symbol.base}")
    private String baseSymbol;

    @Value("${symbol.quote}")
    private String quoteSymbol;

    @Value("${symbol.minimum-order}")
    private BigDecimal minimumOrderValue;

    public String getBaseSymbol() {
        return baseSymbol;
    }

    public Symbol setBaseSymbol(final String baseSymbol) {
        this.baseSymbol = baseSymbol;
        return this;
    }

    public String getQuoteSymbol() {
        return quoteSymbol;
    }

    public Symbol setQuoteSymbol(final String quoteSymbol) {
        this.quoteSymbol = quoteSymbol;
        return this;
    }

    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public Symbol setMinimumOrderValue(final BigDecimal minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
        return this;
    }

    public String getSymbol() {
        return format("%s%s", baseSymbol, quoteSymbol);
    }
}

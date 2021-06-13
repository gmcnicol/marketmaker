package io.nkdtrdr.mrktmkr.symbols;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
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

    @Value("${order-price-adjustment}")
    private BigDecimal orderPriceAdjustment;

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

    public BigDecimal getOrderPriceAdjustment() {
        return orderPriceAdjustment;


    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equal(getBaseSymbol(), symbol.getBaseSymbol()) && Objects.equal(getQuoteSymbol(),
                symbol.getQuoteSymbol()) && Objects.equal(getMinimumOrderValue(), symbol.getMinimumOrderValue()) && Objects.equal(getOrderPriceAdjustment(), symbol.getOrderPriceAdjustment());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getBaseSymbol(), getQuoteSymbol(), getMinimumOrderValue(), getOrderPriceAdjustment());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("baseSymbol", baseSymbol)
                .add("quoteSymbol", quoteSymbol)
                .add("minimumOrderValue", minimumOrderValue)
                .add("orderPriceAdjustment", orderPriceAdjustment)
                .toString();
    }
}

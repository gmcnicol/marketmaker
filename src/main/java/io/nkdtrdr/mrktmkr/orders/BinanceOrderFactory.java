package io.nkdtrdr.mrktmkr.orders;

import com.binance.api.client.domain.account.NewOrder;

import java.math.BigDecimal;
import java.util.Locale;

import static com.binance.api.client.domain.TimeInForce.GTC;

public final class BinanceOrderFactory {
    private BinanceOrderFactory() {

    }

    public static NewOrder limitBuy(String symbol, BigDecimal price, BigDecimal quantity) {
        return NewOrder.limitBuy(symbol.toUpperCase(Locale.ROOT),
                GTC,
                quantity.toPlainString(),
                price.toPlainString());
    }

    public static NewOrder limitSell(String symbol, BigDecimal price, BigDecimal quantity) {
        return NewOrder.limitSell(symbol, GTC, quantity.toPlainString(), price.toPlainString());
    }
}

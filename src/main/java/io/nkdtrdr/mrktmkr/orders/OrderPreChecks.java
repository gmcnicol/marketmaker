package io.nkdtrdr.mrktmkr.orders;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class OrderPreChecks {

    private final AccountFacade accountBalanceCache;
    private final Symbol symbol;

    public OrderPreChecks(final AccountFacade accountBalanceCache,
                          @Lazy Symbol symbol) {
        this.accountBalanceCache = accountBalanceCache;
        this.symbol = symbol;
    }

    public boolean accountCanAffordOrder(Order order) {

        final BigDecimal baseFree = accountBalanceCache.getFreeBalanceForAsset(symbol.getBaseSymbol());
        final BigDecimal quoteFree = accountBalanceCache.getFreeBalanceForAsset(symbol.getQuoteSymbol());

        if (order.getSide().equals(Order.OrderSide.SELL)) {
            return order.getQuantity().compareTo(baseFree) <= 0;
        }

        if (order.getSide().equals(Order.OrderSide.BUY)) {
            return order.getValue().compareTo(quoteFree) <= 0;
        }

        return false;
    }

    public boolean orderHasEnoughValue(Order order) {
        return order.getValue().compareTo(symbol.getMinimumOrderValue()) >= 0;
    }
}

package io.nkdtrdr.mrktmkr.orders;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class OrderPreChecks {

    private final AccountFacade accountBalanceCache;

    public OrderPreChecks(final AccountFacade accountBalanceCache) {
        this.accountBalanceCache = accountBalanceCache;
    }

    public boolean accountCanAffordOrder(Order order) {
        final BigDecimal btc = accountBalanceCache.getFreeBalanceForAsset("BTC");
        final BigDecimal gbp = accountBalanceCache.getFreeBalanceForAsset("GBP");

        if (order.getSide().equals(Order.OrderSide.SELL)) {
            return order.getQuantity().compareTo(btc) <= 0;
        }

        if (order.getSide().equals(Order.OrderSide.BUY)) {
            return order.getValue().compareTo(gbp) <= 0;
        }

        return false;
    }

    public boolean orderHasEnoughValue(Order order) {
        return order.getValue().compareTo(BigDecimal.valueOf(10.10D)) >= 0;
    }
}

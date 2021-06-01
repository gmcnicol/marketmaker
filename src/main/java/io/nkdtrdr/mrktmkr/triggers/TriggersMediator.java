package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static io.nkdtrdr.mrktmkr.utilities.DateUtils.formattedDateString;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.now;


@Component
public class TriggersMediator {
    private static final BigDecimal MINIMUM_ORDER = valueOf(10.10);
    private final AccountFacade accountFacade;

    public TriggersMediator(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    public Collection<Order> getTriggersForOrder(Order order) {
        if (order.getValue().compareTo(MINIMUM_ORDER) > 0) {
            if (order.getSide().equals(Order.OrderSide.BUY)) {
                order.setStrategy("BUY");
                final BigDecimal buyCommission =
                        ONE.subtract(getBuyCommission()).setScale(4, RoundingMode.FLOOR);

                final BigDecimal netQuantity =
                        order.getQuantity().multiply(buyCommission).setScale(6, RoundingMode.FLOOR);

                final BigDecimal saleCommission = ONE.add(getSaleCommission());
                final BigDecimal margin = valueOf(1.0003);
                final BigDecimal adjustedValue = order.getValue().multiply(saleCommission).multiply(margin);
                final BigDecimal price = adjustedValue.divide(netQuantity, 2, RoundingMode.CEILING);
                order.setOrderTrigger(Order.OrderTrigger.PRICE);
                order.setPrice(price);
                order.setQuantity(netQuantity);
                order.setSide(Order.OrderSide.SELL);
                order.setOrderId("FSELL" + formattedDateString(now()));
                return Set.of(order);
            } else if (order.getSide().equals(Order.OrderSide.SELL)) {
                order.setStrategy("SELL");
                final BigDecimal saleCommission = ONE.subtract(getSaleCommission());
                final BigDecimal netValue = order.getValue().multiply(saleCommission).setScale(2, RoundingMode.FLOOR);

                final BigDecimal buyCommission = ONE.add(getBuyCommission());
                final BigDecimal grossQuantity = order.getQuantity().multiply(buyCommission).setScale(6,
                        RoundingMode.CEILING);

                final BigDecimal margin = valueOf(0.9997);
                final BigDecimal newValue = netValue.multiply(margin);
                final BigDecimal newPrice = newValue.divide(grossQuantity, 2, RoundingMode.FLOOR);

                order.setOrderTrigger(Order.OrderTrigger.PRICE);
                order.setPrice(newPrice);
                order.setQuantity(grossQuantity);
                order.setSide(Order.OrderSide.BUY);
                order.setOrderId("FBUY" + formattedDateString(now()));
                return Set.of(order);
            }
        }

        return Collections.emptySet();
    }

    private BigDecimal getSaleCommission() {
        return accountFacade.getSaleCommission();
    }

    private BigDecimal getBuyCommission() {
        return accountFacade.getBuyCommission();
    }

    public BigDecimal getFreeBalanceForAsset(String asset) {
        return accountFacade.getFreeBalanceForAsset(asset);
    }
}

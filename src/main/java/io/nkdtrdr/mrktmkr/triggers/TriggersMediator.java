package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static io.nkdtrdr.mrktmkr.utilities.DateUtils.formattedDateString;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.CEILING;
import static java.time.LocalDateTime.now;


@Component
public class TriggersMediator {
    private static final BigDecimal MINIMUM_ORDER = valueOf(10.10);
    private final AccountFacade accountFacade;
    private final Symbol symbol;

    public TriggersMediator(AccountFacade accountFacade, final Symbol symbol) {
        this.accountFacade = accountFacade;
        this.symbol = symbol;
    }

    public Collection<Order> getTriggersForOrder(Order order) {
        final HashSet<Order> orders = new HashSet<>(2);
        if (order.getValue().compareTo(MINIMUM_ORDER) > 0) {
            orders.addAll(getFollowupSales(order));
            orders.addAll(getFollowupBuys(order));
        }
        return orders;
    }

    private Set<Order> getFollowupBuys(final Order order) {
        if (order.getSide().equals(Order.OrderSide.SELL)) {
            order.setStrategy("SELL");
            final OrderStrings orderStrings = OrderStrings.fromOrder(order);
            final String orderId = "FBUY" + formattedDateString(now());

            final BigDecimal saleCommission = ONE.subtract(getMakerCommission());
            BigDecimal netValue =
                    new BigDecimal(orderStrings.originalValue)
                            .multiply(saleCommission)
                            .setScale(symbol.getQuoteScale(), RoundingMode.FLOOR);

            final BigDecimal buyCommission = ONE.add(getMakerCommission());
            final BigDecimal grossQuantity =
                    new BigDecimal(orderStrings.originalQuantity).multiply(buyCommission).setScale(symbol.getBaseScale(),
                            CEILING);

            BigDecimal margin = valueOf(0.9999);
            BigDecimal newValue = netValue.multiply(margin);
            BigDecimal newPrice = newValue.divide(grossQuantity, symbol.getQuoteScale(), RoundingMode.FLOOR);

            Order.Builder orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.INTENDED);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(new BigDecimal(newPrice.toString()));
            orderBuilder.setQuantity(grossQuantity);
            orderBuilder.setSide(Order.OrderSide.BUY);
            orderBuilder.setOrderId(orderId);
            final Order targetOrder = orderBuilder.build();
            return Set.of(targetOrder);
        }
        return Set.of();
    }

    private Set<Order> getFollowupSales(final Order order) {
        if (order.getSide().equals(Order.OrderSide.BUY)) {
            final OrderStrings orderStrings = OrderStrings.fromOrder(order);
            order.setStrategy("BUY");
            final Integer quoteScale = symbol.getQuoteScale();
            final BigDecimal makerCommission = getMakerCommission();
            final BigDecimal grossCommission = ONE.add(makerCommission).setScale(symbol.getBaseScale(), CEILING);
            final BigDecimal netCommission = ONE.subtract(makerCommission).setScale(quoteScale, RoundingMode.FLOOR);

            final BigDecimal netQuantity =
                    new BigDecimal(orderStrings.originalQuantity).multiply(netCommission).setScale(symbol.getBaseScale(),
                            RoundingMode.DOWN);

            final BigDecimal margin = valueOf(1.0005);
            final BigDecimal price = new BigDecimal(orderStrings.originalValue)
                    .multiply(margin)
                    .multiply(grossCommission)
                    .divide(netQuantity, quoteScale, CEILING);

            Order.Builder orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.INTENDED);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(price);
            orderBuilder.setQuantity(netQuantity);
            orderBuilder.setSide(Order.OrderSide.SELL);
            final String orderId = "FSELL" + formattedDateString(now());
            orderBuilder.setOrderId(orderId);
            final Order targetOrder = orderBuilder.build();

            return Set.of(targetOrder);
        }
        return Set.of();
    }

    public BigDecimal getMakerCommission() {
        return accountFacade.getMakerCommission();
    }

    private static class OrderStrings {
        private final String originalValue;
        private final String originalQuantity;

        private OrderStrings(final String originalValue, final String originalQuantity) {
            this.originalValue = originalValue;
            this.originalQuantity = originalQuantity;
        }

        private static OrderStrings fromOrder(Order order) {
            return new OrderStrings(
                    order.getValue().toString(),
                    order.getQuantity().toString());
        }
    }
}

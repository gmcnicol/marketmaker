package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
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
            final BigDecimal originalValue = order.getValue();
            final String orderId = "FBUY" + formattedDateString(now());
            final BigDecimal saleCommission = ONE.subtract(getSaleCommission());
            BigDecimal netValue =
                    originalValue.multiply(saleCommission).setScale(2, RoundingMode.FLOOR);

            final BigDecimal buyCommission = ONE.add(getBuyCommission());
            final BigDecimal grossQuantity =
                    order.getQuantity().multiply(buyCommission).setScale(6, RoundingMode.CEILING);

            BigDecimal margin = valueOf(0.9997);
            BigDecimal newValue = netValue.multiply(margin);
            BigDecimal newPrice = newValue.divide(grossQuantity, 2, RoundingMode.FLOOR);

            Order.Builder orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.FROM_ABOVE);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(newPrice);
            orderBuilder.setQuantity(grossQuantity);
            orderBuilder.setSide(Order.OrderSide.BUY);
            orderBuilder.setOrderId(orderId);
            final Order targetOrder = orderBuilder.build();

            margin = valueOf(1.0017);
            newPrice = order.getPrice().multiply(margin);

            orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.FROM_BELOW);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(newPrice);
            orderBuilder.setQuantity(grossQuantity);
            orderBuilder.setSide(Order.OrderSide.BUY);
            orderBuilder.setOrderId(orderId);

            final Order bailOrder = orderBuilder.build();
            return Set.of(targetOrder, bailOrder);
        }
        return Set.of();
    }

    private Set<Order> getFollowupSales(final Order order) {
        if (order.getSide().equals(Order.OrderSide.BUY)) {
            final BigDecimal originalValue = order.getValue();
            order.setStrategy("BUY");
            final BigDecimal buyCommission =
                    ONE.subtract(getBuyCommission()).setScale(4, RoundingMode.FLOOR);

            final BigDecimal netQuantity =
                    order.getQuantity().multiply(buyCommission).setScale(6, RoundingMode.FLOOR);

            final BigDecimal saleCommission = ONE.add(getSaleCommission());
            BigDecimal margin = valueOf(1.0003);
            BigDecimal adjustedValue = originalValue.multiply(saleCommission).multiply(margin);
            BigDecimal price = adjustedValue.divide(netQuantity, 2, RoundingMode.CEILING);

            Order.Builder orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.FROM_BELOW);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(price);
            orderBuilder.setQuantity(netQuantity);
            orderBuilder.setSide(Order.OrderSide.SELL);
            final String orderId = "FSELL" + formattedDateString(now());
            orderBuilder.setOrderId(orderId);
            final Order targetOrder = orderBuilder.build();

            margin = valueOf(0.9983);
            price = order.getPrice().multiply(margin);
            orderBuilder = Order.newBuilder(order);
            orderBuilder.setTriggerDirection(Order.TriggerDirection.FROM_ABOVE);
            orderBuilder.setOrderTrigger(Order.OrderTrigger.PRICE);
            orderBuilder.setPrice(price);
            orderBuilder.setQuantity(netQuantity);
            orderBuilder.setSide(Order.OrderSide.SELL);
            orderBuilder.setOrderId(orderId);
            final Order bailOrder = orderBuilder.build();
            return Set.of(targetOrder, bailOrder);
        }
        return Set.of();
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

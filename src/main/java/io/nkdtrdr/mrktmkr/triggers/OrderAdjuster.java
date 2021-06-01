package io.nkdtrdr.mrktmkr.triggers;

import io.nkdtrdr.mrktmkr.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static io.nkdtrdr.mrktmkr.dto.Order.newBuilder;
import static io.nkdtrdr.mrktmkr.utilities.OrderCalculations.aCalculator;
import static java.math.BigDecimal.valueOf;
import static java.math.BigDecimal.*;
import static java.math.RoundingMode.*;


/**
 * Utility class to adjust big orders into smaller ones at the current price.
 */

@Component
public class OrderAdjuster {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAdjuster.class);
    private TriggersMediator triggersMediator;

    OrderAdjuster() {

    }

    public void setTriggersMediator(TriggersMediator mediator) {
        this.triggersMediator = mediator;
    }

    Collection<Order> adjustBuyOrderToPrice(Order order, BigDecimal newPrice) {

        final BigDecimal freeQuote = triggersMediator.getFreeBalanceForAsset("GBP");

        final BigDecimal originalValue = order.getValue();
        final Adjustment orderAdjustment = IntStream.range(2, 100).boxed()
                .map(Adjustment::new)
                .map(adjustment ->
                        adjustment.setValue(
                                originalValue.multiply(ONE.divide(valueOf(adjustment.numberOfOrders), 2, FLOOR))))
                .map(adjustment -> adjustment.setQuantity(aCalculator()
                        .withPrice(newPrice)
                        .withCost(adjustment.value)
                        .withRounding(FLOOR)
                        .getQuantity()))
                .filter(adjustment -> adjustment.value.compareTo(freeQuote) <= 0)
                .filter(adjustment -> adjustment.value.compareTo(valueOf(10.10)) >= 0)
                .filter(adjustment -> adjustment.value.compareTo(valueOf(50))<0)
                .filter(adjustment -> adjustment.value.multiply(valueOf(adjustment.numberOfOrders)).compareTo(originalValue) <= 0)
                .findFirst().orElse(new Adjustment(0));

        final ArrayList<Order> orders = new ArrayList<>(orderAdjustment.numberOfOrders);
        for (int i = 0; i < orderAdjustment.numberOfOrders; i++) {
            orders.add(newBuilder(order)
                    .setValue(null)
                    .setPrice(newPrice)
                    .setQuantity(orderAdjustment.quantity).build());
        }
        return orders;
    }

    Collection<Order> adjustSellOrderToPrice(Order order, BigDecimal newPrice) {
        final BigDecimal freeBTC = triggersMediator.getFreeBalanceForAsset("BTC");
        BigDecimal originalCost = order.getValue();

        Adjustment orderAdjustment;

        orderAdjustment = IntStream.range(2, 100).boxed()
                .map(Adjustment::new)
                .map(a -> a.setValue(originalCost.multiply(ONE.divide(valueOf(a.numberOfOrders), 2, UP))))
                .filter(adjustment -> adjustment.value.compareTo(valueOf(10.10D)) >= 0)
                .map(adjustment ->
                        adjustment.setQuantity(aCalculator()
                                .withPrice(newPrice)
                                .withCost(adjustment.value)
                                .withRounding(CEILING).getQuantity()))
                .filter(adjustment -> adjustment.quantity.compareTo(freeBTC) <= 0)
                .filter(adjustment -> adjustment.value.compareTo(valueOf(50))<0)
                .filter(adjustment -> adjustment.value.multiply(valueOf(adjustment.numberOfOrders)).compareTo(originalCost) > 0)
                .findFirst().orElse(new Adjustment(0));

        final int numberOfOrders = orderAdjustment.numberOfOrders;
        final List<Order> orders = new ArrayList<>(numberOfOrders);
        for (int i = 0; i < numberOfOrders; i++) {
            orders.add(newBuilder(order)
                    .setValue(null)
                    .setPrice(newPrice)
                    .setQuantity(orderAdjustment.quantity).build());
        }

        return orders;
    }

    Order consolidateOrdersOnQuantity(Collection<Order> orders, BigDecimal price) {

        final BigDecimal totalQuantity = orders.stream()
                .map(Order::getQuantity)
                .reduce(ZERO, BigDecimal::add);

        return orders.stream().findFirst()
                .map(order -> newBuilder(order)
                        .setQuantity(totalQuantity)
                        .setPrice(price).build())
                .orElse(null);
    }

    Order consolidateOrdersOnValue(Collection<Order> orders, BigDecimal price) {
        final BigDecimal value = orders.stream()
                .map(Order::getValue)
                .reduce(ZERO, BigDecimal::add);

        final BigDecimal quantity =
                aCalculator()
                        .withRounding(UP)
                        .withCost(value)
                        .withPrice(price)
                        .getQuantity();

        final Order result = orders.stream().findFirst()
                .map(order -> newBuilder(order)
                        .setQuantity(quantity)
                        .setPrice(price).build())
                .orElse(null);
        LOGGER.debug("console " +
                "price {} orders: (£{}){}: result: {}", price, value.toPlainString(), orders, result);
        return result;
    }

    private static class Adjustment {
        int numberOfOrders;
        BigDecimal value;
        BigDecimal quantity;

        public Adjustment(final int numberOfOrders) {
            this.numberOfOrders = numberOfOrders;
        }

        Adjustment setValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        Adjustment setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }
    }
}

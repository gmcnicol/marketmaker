package io.nkdtrdr.mrktmkr.orders;

import com.google.common.base.MoreObjects;
import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.nkdtrdr.mrktmkr.dto.Order.orderIsABuy;
import static io.nkdtrdr.mrktmkr.dto.Order.orderIsASell;
import static io.nkdtrdr.mrktmkr.utilities.BigDecimalUtilities.getBigDecimal;


@Component
public class OrdersMediator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersMediator.class);
    private static final BigDecimal BIT_DIFFERENCE = getBigDecimal("0.0002");
    private final ConcurrentSkipListMap<BigDecimal, Order> triggerSales;
    private final ConcurrentSkipListMap<BigDecimal, Order> triggerBuys;
    private final LiveOrderTracker liveBuys;
    private final LiveOrderTracker liveSells;
    private final OrderPreChecks orderPreChecks;
    private final AccountFacade accountFacade;
    private BigDecimal bestAsk;
    private BigDecimal bestBid;

    public OrdersMediator(final OrderPreChecks orderPreChecks, final AccountFacade accountFacade) {
        this.orderPreChecks = orderPreChecks;
        this.accountFacade = accountFacade;

        this.triggerSales = new ConcurrentSkipListMap<>();
        this.triggerBuys = new ConcurrentSkipListMap<>();
        bestAsk = BigDecimal.ZERO;
        bestBid = BigDecimal.ZERO;
        liveBuys = new LiveOrderTracker();
        liveSells = new LiveOrderTracker();
    }

    private static BiFunction<BigDecimal, Order, Order> bumpOrderQuantity(final Order order) {
        return (p, o) -> {
            o.setQuantity(o.getQuantity().add(order.getQuantity()));
            return o;
        };
    }

    public void addTriggeredBuy(final Order order) {
        triggerBuys.computeIfPresent(order.getPrice(), bumpOrderQuantity(order));
        triggerBuys.putIfAbsent(order.getPrice(), order);
        LOGGER.debug("Trigger Buy {}", this.triggersString());
    }

    public boolean strategyHasTrigger(String strategy) {
        final boolean buyMatch = triggerBuys.values().stream().anyMatch(order -> order.getStrategy().equals(strategy));
        final boolean saleMatch =
                triggerSales.values().stream().anyMatch(order -> order.getStrategy().equals(strategy));

        return buyMatch || saleMatch;
    }

    public void addTriggeredSale(final Order order) {
        triggerSales.computeIfPresent(order.getPrice(), bumpOrderQuantity(order));
        triggerSales.putIfAbsent(order.getPrice(), order);
        LOGGER.debug("Trigger Sale {}", this.triggersString());
    }

    public Collection<Order> getTriggeredBuys() {
        final BigDecimal bestAsk = getBestAsk();

        return triggerBuys.tailMap(bestAsk, true)
                .values().stream()
                .filter(order -> !liveBuys.orderExists(order.getOrderId()))
                .map(o -> Order.newBuilder(o)
                        .setQuantity(o.getQuantity())
                        .setPrice(bestAsk).build())
                .filter(orderPreChecks::orderHasEnoughValue)
                .filter(orderPreChecks::accountCanAffordOrder)
                .peek(order -> LOGGER.info("TRIGGER BUY {} £{}", order.getOrderId(), order.getValue()))
                .collect(Collectors.toSet());
    }

    BigDecimal getBestBid() {
        return bestBid;
    }

    void setBestBid(String bestBid) {
        this.bestBid = getBigDecimal(bestBid);
    }

    public boolean isOrderIdOpen(final String orderId) {
        return liveSells.orderExists(orderId) || liveBuys.orderExists(orderId);
    }

    public void trackOpenOrder(final Order order) {
        if (orderIsABuy(order))
            liveBuys.trackOrder(order);
        if (orderIsASell(order))
            liveSells.trackOrder(order);
    }

    public void stopTrackingOrder(final Order order) {
        liveBuys.cancelOrder(order);
        liveSells.cancelOrder(order);
    }

    public void removeTrigger(final String orderId) {
        triggerBuys.values().removeIf(o -> o.getOrderId().equals(orderId));
        triggerSales.values().removeIf(o -> o.getOrderId().equals(orderId));
    }

    public Collection<Order> getTriggeredSells() {
        final BigDecimal bestBid = getBestBid();

        return triggerSales.headMap(bestBid, true).values()
                .stream()
                .filter(o -> !liveSells.orderExists(o.getOrderId()))
                .map(o -> Order.newBuilder(o)
                        .setQuantity(o.getQuantity()).setPrice(bestBid).build())
                .filter(orderPreChecks::orderHasEnoughValue)
                .filter(orderPreChecks::accountCanAffordOrder)
                .peek(order -> LOGGER.info("TRIGGER SALE {} £{}", order.getOrderId(), order.getValue()))
                .collect(Collectors.toSet());
    }

    BigDecimal getBestAsk() {
        return bestAsk;
    }

    void setBestAsk(String bestAsk) {
        this.bestAsk = getBigDecimal(bestAsk);
    }

    public Collection<String> getOrderIdsToCancel() {
        final BigDecimal sellRange = getBestBid().multiply(BIT_DIFFERENCE);
        Predicate<BigDecimal> sellsToCancel = p -> p.subtract(getBestAsk()).abs().compareTo(sellRange) > 0;
        final Stream<String> stream = liveSells.getOrderIdsToCancel(sellsToCancel).stream();

        final BigDecimal buyRange = getBestAsk().multiply(BIT_DIFFERENCE);
        Predicate<BigDecimal> buyOutOfRange = p -> p.subtract(getBestBid()).abs().compareTo(buyRange) > 0;
        final Stream<String> buyStream = liveBuys.getOrderIdsToCancel(buyOutOfRange).stream();

        return Stream.of(buyStream, stream).flatMap(Function.identity())
                .collect(Collectors.toSet());
    }

    public void adjustTriggersForOrder(final Order order) {
        if (orderIsABuy(order)) {
            adjustBuyTriggers(order);
        } else if (orderIsASell(order)) {
            adjustSellTriggers(order);
        }

        LOGGER.debug("Adjusted Triggers {}", this.triggersString());
    }

    private void adjustSellTriggers(final Order order) {
        final Collection<Order> orders = triggerSales.headMap(order.getValue()).values();
        Set<String> ordersToRemove = new HashSet<>();
        for (Order o : orders) {
            if (order.getValue().compareTo(BigDecimal.valueOf(10.10D)) <= 0) break;
            if (o.getValue().compareTo(order.getValue()) <= 0) {
                ordersToRemove.add(o.getOrderId());
                final BigDecimal subtract = order.getValue().subtract(o.getValue());
                order.setValue(subtract);
            }
            if (o.getValue().compareTo(order.getValue()) > 0) {
                final BigDecimal subtract = o.getValue().subtract(order.getValue());
                o.setValue(subtract);
                order.setValue(BigDecimal.ZERO);
            }
        }
        ordersToRemove.forEach(this::removeTrigger);
    }

    private void adjustBuyTriggers(final Order order) {
        final Collection<Order> orders = triggerBuys.tailMap(order.getValue(), true).values();
        Set<String> ordersToRemove = new HashSet<>();
        for (Order o : orders) {
            if (order.getValue().compareTo(BigDecimal.valueOf(10.10)) <= 0) break;
            order.setValue(null);
            if (o.getQuantity().compareTo(order.getQuantity()) <= 0) {
                ordersToRemove.add(o.getOrderId());
                final BigDecimal subtract = order.getQuantity().subtract(o.getQuantity());
                order.setQuantity(subtract);
            }
            if (o.getQuantity().compareTo(order.getQuantity()) > 0) {
                final BigDecimal subtract = o.getQuantity().subtract(order.getQuantity());
                o.setQuantity(subtract);
                order.setQuantity(BigDecimal.ZERO);
            }
        }
        ordersToRemove.forEach(this::removeTrigger);
    }

    private String triggersString() {
        return MoreObjects.toStringHelper(this)
                .addValue('\n')
                .add("Sales", triggerSales)
                .addValue('\n')
                .add("Buys", triggerBuys).omitNullValues().toString();
    }
}

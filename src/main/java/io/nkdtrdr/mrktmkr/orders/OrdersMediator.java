package io.nkdtrdr.mrktmkr.orders;

import com.google.common.base.MoreObjects;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.nkdtrdr.mrktmkr.dto.Order.TriggerDirection.BAIL_OUT;
import static io.nkdtrdr.mrktmkr.dto.Order.TriggerDirection.INTENDED;
import static io.nkdtrdr.mrktmkr.dto.Order.orderIsABuy;
import static io.nkdtrdr.mrktmkr.dto.Order.orderIsASell;
import static io.nkdtrdr.mrktmkr.utilities.BigDecimalUtilities.getBigDecimal;
import static java.util.function.Function.identity;


@Component
public class OrdersMediator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersMediator.class);
    private static final BigDecimal BIT_DIFFERENCE = getBigDecimal("0.0002");
    private final ConcurrentSkipListMap<BigDecimal, Order> triggerSales;
    private final ConcurrentSkipListMap<BigDecimal, Order> triggerBuys;
    private final LiveOrderTracker liveBuys;
    private final LiveOrderTracker liveSells;
    private final OrderPreChecks orderPreChecks;
    private BigDecimal bestAsk;
    private BigDecimal bestBid;

    public OrdersMediator(final OrderPreChecks orderPreChecks) {
        this.orderPreChecks = orderPreChecks;

        this.triggerSales = new ConcurrentSkipListMap<>();
        this.triggerBuys = new ConcurrentSkipListMap<>();
        bestAsk = BigDecimal.ZERO;
        bestBid = BigDecimal.ZERO;
        liveBuys = new LiveOrderTracker();
        liveSells = new LiveOrderTracker();
    }

    private static BiFunction<BigDecimal, Order, Order> bumpOrderQuantity(final Order order) {
        return (p, o) -> {
            if (o.getOrderId().equals(order.getOrderId())) return o;
            o.setQuantity(o.getQuantity().add(order.getQuantity()));
            return o;
        };
    }

    public void addTriggeredBuy(final Order order) {
        triggerBuys.computeIfPresent(order.getPrice(), bumpOrderQuantity(order));
        triggerBuys.putIfAbsent(order.getPrice(), order);
        LOGGER.info("Added Trigger Buy {}", this.triggersString());
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
        LOGGER.info("Added Trigger Sale {}", this.triggersString());
    }

    public Collection<Order> getTriggeredBuys() {
        final BigDecimal bestAsk = getBestAsk();
        return Stream.of(
                triggerBuys.tailMap(bestAsk, true).values().stream()
                        .filter(order -> directionMatches(order, INTENDED)),
                triggerBuys.headMap(bestAsk, true).values().stream()
                        .filter(order -> directionMatches(order, BAIL_OUT)))
                .flatMap(identity())
                .filter(order -> !liveBuys.orderExists(order.getOrderId()))
                .filter(order -> Order.OrderSide.BUY.equals(order.getSide()))
                .map(o -> Order.newBuilder(o).setPrice(bestAsk).build())
                .filter(orderPreChecks::orderHasEnoughValue)
                .filter(orderPreChecks::accountCanAffordOrder)
                .peek(order -> LOGGER.info("TRIGGER BUY {}", order))
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

        return Stream.of(triggerSales.headMap(bestBid, true).values().stream().filter(order -> directionMatches(order
                , INTENDED)),
                this.triggerSales.tailMap(bestBid, true).values().stream().filter(order -> directionMatches(order,
                        BAIL_OUT)))
                .flatMap(identity())
                .filter(o -> !liveSells.orderExists(o.getOrderId()))
                .filter(order -> Order.OrderSide.SELL.equals(order.getSide()))
                .map(o -> Order.newBuilder(o).setPrice(bestBid).build())
                .filter(orderPreChecks::orderHasEnoughValue)
                .filter(orderPreChecks::accountCanAffordOrder)
                .peek(order -> LOGGER.info("TRIGGER SALE {} ", order.getValue()))
                .collect(Collectors.toSet());
    }

    private boolean directionMatches(final Order order, final Order.TriggerDirection fromAbove) {
        return fromAbove.equals(order.getTriggerDirection());
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

        return Stream.of(buyStream, stream).flatMap(identity())
                .collect(Collectors.toSet());
    }

    private String triggersString() {
        return MoreObjects.toStringHelper(this)
                .addValue('\n')
                .add("Sales", triggerSales)
                .addValue('\n')
                .add("Buys", triggerBuys).omitNullValues().toString();
    }
}

package io.nkdtrdr.mrktmkr.orders;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.dto.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.nkdtrdr.mrktmkr.disruptor.EventEnvelope.EventEnvelopeBuilder.anEventEnvelope;
import static java.util.function.Function.identity;


@Component
public class OrdersFacade {
    public static final String ORDER_REQUESTED = "ORDER_REQUESTED";
    private final ProcessMediator processMediator;
    private final OrdersMediator ordersMediator;
    private final OrderPreChecks orderPreChecks;

    public OrdersFacade(ProcessMediator processMediator,
                        final OrdersMediator ordersMediator,
                        final OrderPreChecks orderPreChecks) {
        this.processMediator = processMediator;
        this.ordersMediator = ordersMediator;
        this.orderPreChecks = orderPreChecks;
    }

    public static EventEnvelope eventEnvelopFromOrder(Order order) {
        return anEventEnvelope()
                .withEventName(ORDER_REQUESTED)
                .withPayload(order)
                .build();
    }

    private static EventEnvelope cancelEventEnvelopeFromOrder(String order) {
        return anEventEnvelope()
                .withEventName("CANCEL_REQUESTED")
                .withPayload(order)
                .build();
    }

    public BigDecimal getBestBidPrice() {
        return ordersMediator.getBestBid();
    }

    public BigDecimal getBestAskPrice() {
        return ordersMediator.getBestAsk();
    }

    public void setBestBidPrice(String bestBidPrice, Consumer<EventEnvelope> callback) {
        ordersMediator.setBestBid(bestBidPrice);
        ordersMediator.getTriggeredSells().stream()
                .peek(order -> order.setOrderTrigger(Order.OrderTrigger.IMMEDIATE))
                .map(OrdersFacade::eventEnvelopFromOrder)
                .forEach(callback);

        callBackCancels(callback);
    }

    private void callBackCancels(final Consumer<EventEnvelope> callback) {
        ordersMediator.getOrderIdsToCancel().stream()
                .map(OrdersFacade::cancelEventEnvelopeFromOrder)
                .forEach(callback);
    }

    public void stopTrackingOrderId(final String orderId) {
        ordersMediator.stopTrackingOrder(Order.newBuilder().setOrderId(orderId).build());
    }

    public void setBestAskPrice(String bestAskPrice, final Consumer<EventEnvelope> callback) {
        this.ordersMediator.setBestAsk(bestAskPrice);
        ordersMediator.getTriggeredBuys().stream()
                .peek(order -> order.setOrderTrigger(Order.OrderTrigger.IMMEDIATE))
                .peek(order -> order.setPrice(ordersMediator.getBestAsk()))
                .map(OrdersFacade::eventEnvelopFromOrder)
                .forEach(callback);

        callBackCancels(callback);
    }

    public void placeOrder(final Order o) {
        processMediator.processEvent(ORDER_REQUESTED, o);
    }

    public void addOrderToBeTriggered(final Order order) {

        if (Order.TriggerDirection.FROM_BELOW.equals(order.getTriggerDirection()))
            ordersMediator.addTriggeredSale(order);
        else if (Order.TriggerDirection.FROM_ABOVE.equals(order.getTriggerDirection()))
            ordersMediator.addTriggeredBuy(order);
    }

    public void reTriggerOrder(final String orderId, final Consumer<EventEnvelope> callback) {
        Stream.of(ordersMediator.getTriggeredBuys().stream(),
                ordersMediator.getTriggeredSells().stream())
                .flatMap(identity())
                .filter(order -> order.getOrderId().equals(orderId))
                .map(OrdersFacade::eventEnvelopFromOrder)
                .forEach(callback);
    }

    public void removeTrigger(final String orderId) {
        ordersMediator.removeTrigger(orderId);
    }

    public void trackOpenOrder(final Order order) {
        ordersMediator.trackOpenOrder(order);
    }

    public boolean isOrderIdOpen(String orderId) {
        return ordersMediator.isOrderIdOpen(orderId);
    }

    public boolean canPlaceOrder(Order order) {
        return order != null &&
                orderPreChecks.accountCanAffordOrder(order)
                && orderPreChecks.orderHasEnoughValue(order);
    }

    public void adjustTriggersForOrder(final Order order) {
        ordersMediator.adjustTriggersForOrder(order);
    }

    public boolean strategyHasTrigger(final String strategy) {
        return ordersMediator.strategyHasTrigger(strategy);
    }

    public void setLastTickerUpdate(final long lastTickerUpdate) {
        processMediator.setLastTickerUpdate(lastTickerUpdate);
    }
}

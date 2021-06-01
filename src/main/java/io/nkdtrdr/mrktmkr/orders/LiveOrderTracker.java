package io.nkdtrdr.mrktmkr.orders;

import io.nkdtrdr.mrktmkr.dto.Order;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LiveOrderTracker {

    private final Map<BigDecimal, String> orders = new ConcurrentHashMap<>();

    public void trackOrder(final Order order) {
        orders.putIfAbsent(order.getPrice(), order.getOrderId());
    }

    public void cancelOrder(final Order order) {
        orders.values().removeIf(f -> f.equals(order.getOrderId()));
    }

    public Collection<String> getOrderIdsToCancel(Predicate<BigDecimal> predicate) {
        return orders.entrySet().stream()
                .filter(e -> predicate.test(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    public boolean orderExists(String orderId) {
        return orders.containsValue(orderId);
    }
}

package io.nkdtrdr.mrktmkr.orderbook;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import io.nkdtrdr.mrktmkr.ProcessMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;

@Component
public class OrderBookCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookCache.class);
    private final NavigableMap<BigDecimal, BigDecimal> bids;
    private final NavigableMap<BigDecimal, BigDecimal> asks;
    private final LinkedList<DepthEvent> pendingEvents = new LinkedList<>();
    private final ProcessMediator processMediator;
    private boolean depthRequested = false;
    Consumer<DepthEvent> depthEventConsumer = this::processPendingDepthEvent;
    private boolean initialised = false;

    public OrderBookCache(ProcessMediator processMediator) {
        this.processMediator = processMediator;

        asks = new TreeMap<>(reverseOrder());
        bids = new TreeMap<>(reverseOrder());
    }

    private static void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
                                        List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
            }
        }
    }

    public Map<BigDecimal, BigDecimal> getBids() {
        return new ConcurrentHashMap<>(bids);
    }

    public Map<BigDecimal, BigDecimal> getAsks() {
        return new ConcurrentHashMap<>(asks);
    }

    public void processDepthEvent(DepthEvent depthEvent) {
        depthEventConsumer.accept(depthEvent);
    }

    private void processPendingDepthEvent(DepthEvent depthEvent) {
        pendingEvents.add(depthEvent);
        if (pendingEvents.size() >= 10 && !depthRequested) {
            depthRequested = true;
            processMediator.getOrderBookForSymbol(depthEvent.getSymbol());
        }
    }

    public void setOrderBook(OrderBook orderBook) {
        orderBook.getAsks().forEach(obe -> asks.put(new BigDecimal(obe.getPrice()), new BigDecimal(obe.getQty())));
        orderBook.getBids().forEach(obe -> bids.put(new BigDecimal(obe.getPrice()), new BigDecimal(obe.getQty())));

        pendingEvents.stream().filter(de -> de.getFinalUpdateId() > orderBook.getLastUpdateId())
                .flatMap(depthEvent -> depthEvent.getAsks().stream())
                .collect(Collectors.toMap(OrderBookEntry::getPrice, OrderBookEntry::getQty))
                .forEach((k, v) -> asks.put(new BigDecimal(k), new BigDecimal(v)));

        pendingEvents.stream().filter(de -> de.getFinalUpdateId() > orderBook.getLastUpdateId())
                .flatMap(depthEvent -> depthEvent.getBids().stream())
                .collect(Collectors.toMap(OrderBookEntry::getPrice, OrderBookEntry::getQty))
                .forEach((k, v) -> bids.put(new BigDecimal(k), new BigDecimal(v)));

        this.depthEventConsumer = this::processDepthEventLive;
        this.initialised = true;
    }

    private void processDepthEventLive(DepthEvent depthEvent) {
        updateOrderBook(bids, depthEvent.getBids());
        updateOrderBook(asks, depthEvent.getAsks());
    }

    public BigDecimal getBestAsk() {
        if (asks.isEmpty()) return BigDecimal.ZERO;
        return asks.lastEntry().getKey();
    }

    public BigDecimal getBestBid() {
        if (bids.isEmpty()) return BigDecimal.ZERO;
        return bids.firstEntry().getKey();
    }

    public boolean isInitialised() {
        return initialised;
    }
}

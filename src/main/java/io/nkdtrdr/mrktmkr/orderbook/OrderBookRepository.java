package io.nkdtrdr.mrktmkr.orderbook;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderBookRepository {
    private final ConcurrentHashMap<String, OrderBookCache> orderBooks = new ConcurrentHashMap<>();
    private final OrderBookCacheFactory factory;

    public OrderBookRepository(OrderBookCacheFactory factory) {
        this.factory = factory;
    }

    public OrderBookCache getForSymbol(String symbol) {

        return orderBooks.computeIfAbsent(symbol.toUpperCase(), s -> factory.build());
    }

    public void setProcessMediator(ProcessMediator processMediator) {
        factory.setMediator(processMediator);
    }
}

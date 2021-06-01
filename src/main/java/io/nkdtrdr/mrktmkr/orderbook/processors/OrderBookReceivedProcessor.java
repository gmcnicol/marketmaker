package io.nkdtrdr.mrktmkr.orderbook.processors;

import com.binance.api.client.domain.market.OrderBook;
import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class OrderBookReceivedProcessor implements EventProcessor {

    private final ProcessMediator processMediator;

    public OrderBookReceivedProcessor(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "ORDER_BOOK_RECEIVED".equals(eventName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final Pair<String, OrderBook> payload = (Pair<String, OrderBook>) makerEvent.getEventEnvelope().getPayload();
        processMediator.setOrderBook(payload.getLeft(), payload.getRight());
    }
}

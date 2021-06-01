package io.nkdtrdr.mrktmkr.orders.processors;

import com.binance.api.client.domain.event.AllMarketTickersEvent;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class TickerEventProcessor implements EventProcessor {

    private final OrdersFacade ordersFacade;

    public TickerEventProcessor(OrdersFacade ordersFacade) {
        this.ordersFacade = ordersFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "TICKER_UPDATED".equals(eventName);
    }

    @Override
    @SuppressWarnings("all")
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final Map<String, Object> payload = (Map<String, Object>) makerEvent.getEventEnvelope().getPayload();
        final AllMarketTickersEvent tickersEvent = (AllMarketTickersEvent) payload.get("payload");

        ordersFacade.setBestAskPrice(tickersEvent.getBestAskPrice(), resultHandler);
        ordersFacade.setBestBidPrice(tickersEvent.getBestBidPrice(), resultHandler);
    }
}

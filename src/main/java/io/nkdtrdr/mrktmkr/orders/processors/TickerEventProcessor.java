package io.nkdtrdr.mrktmkr.orders.processors;

import com.binance.api.client.domain.event.AllMarketTickersEvent;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;


@Component
public class TickerEventProcessor implements EventProcessor {

    private final OrdersFacade ordersFacade;
    private final StrategyFacade strategyFacade;

    public TickerEventProcessor(OrdersFacade ordersFacade, final StrategyFacade strategyFacade) {
        this.ordersFacade = ordersFacade;
        this.strategyFacade = strategyFacade;
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
        ordersFacade.setLastTickerUpdate(Instant.now().toEpochMilli());
        strategyFacade.placeInitialOrder();
    }
}

package io.nkdtrdr.mrktmkr.triggers.processors;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import io.nkdtrdr.mrktmkr.audit.TradeAudit;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import io.nkdtrdr.mrktmkr.triggers.TriggersFacade;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.nkdtrdr.mrktmkr.utilities.BigDecimalUtilities.getBigDecimal;


@Component
public class OrderUpdateEventProcessor implements EventProcessor {
    private final TradeAudit tradeAudit;
    private final TriggersFacade triggersFacade;
    private final Symbol symbol;

    public OrderUpdateEventProcessor(TradeAudit tradeAudit, TriggersFacade triggersFacade, final Symbol symbol) {
        this.tradeAudit = tradeAudit;
        this.triggersFacade = triggersFacade;
        this.symbol = symbol;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "USER_TRADE_UPDATED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final OrderTradeUpdateEvent event = (OrderTradeUpdateEvent) makerEvent.getEventEnvelope().getPayload();

        final boolean shouldProcess =
                Stream.of(OrderStatus.PARTIALLY_FILLED, OrderStatus.FILLED).anyMatch(s -> s.equals(event.getOrderStatus()));
        if (shouldProcess) {
            triggersFacade.setLocked(false);
            tradeAudit.auditOrder(event);
            if (event.getNewClientOrderId().startsWith("F")) return;
            final Order order = Order.newBuilder()
                    .setSymbol(symbol.getSymbol())
                    .setOrderId(event.getNewClientOrderId())
                    .setSide(event.getSide().equals(OrderSide.SELL)
                            ? Order.OrderSide.SELL
                            : Order.OrderSide.BUY)
                    .setQuantity(getBigDecimal(event.getQuantityLastFilledTrade()).setScale(symbol.getBaseScale(),
                            RoundingMode.FLOOR))
                    .setPrice(getBigDecimal(event.getPriceOfLastFilledTrade()))
                    .build();
            triggersFacade.createTriggersForOrder(order, resultHandler);
        }
    }
}

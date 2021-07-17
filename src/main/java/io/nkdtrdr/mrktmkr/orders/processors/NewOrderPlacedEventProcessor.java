package io.nkdtrdr.mrktmkr.orders.processors;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.NewOrderResponse;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static io.nkdtrdr.mrktmkr.utilities.BigDecimalUtilities.getBigDecimal;


@Component
public class NewOrderPlacedEventProcessor implements EventProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderPlacedEventProcessor.class);

    private final OrdersFacade ordersFacade;

    public NewOrderPlacedEventProcessor(final OrdersFacade ordersFacade) {
        this.ordersFacade = ordersFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "NEW_ORDER_PLACED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final NewOrderResponse newOrderResponse = (NewOrderResponse) makerEvent.getEventEnvelope().getPayload();
        final Order order = Order.newBuilder()
                .setPrice(getBigDecimal(newOrderResponse.getPrice()))
                .setOrderId(newOrderResponse.getClientOrderId())
                .setSide(newOrderResponse.getSide().equals(OrderSide.SELL)
                        ? Order.OrderSide.SELL
                        : Order.OrderSide.BUY)
                .setSymbol(newOrderResponse.getSymbol())
                .build();

        ordersFacade.trackOpenOrder(order);
    }
}

package io.nkdtrdr.mrktmkr.orders.processors;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.strategy.StrategyFacade;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
public class ImmediateTriggerOrderRequestedEventProcessor implements EventProcessor {
    private final OrdersFacade ordersFacade;
    private final ProcessMediator processMediator;
    private final StrategyFacade strategyFacade;

    public ImmediateTriggerOrderRequestedEventProcessor(final OrdersFacade ordersFacade,
                                                        final ProcessMediator processMediator,
                                                        final StrategyFacade strategyFacade) {
        this.ordersFacade = ordersFacade;
        this.processMediator = processMediator;
        this.strategyFacade = strategyFacade;
    }

    @Override
    public boolean shouldProcessEventName(final String eventName) {
        return "ORDER_REQUESTED".equals(eventName);
    }

    @Override
    public void process(final MakerEvent makerEvent, final Consumer<EventEnvelope> resultHandler) {
        final Order order = (Order) makerEvent.getEventEnvelope().getPayload();
        if (!ordersFacade.isOrderIdOpen(order.getOrderId())
                && order.getOrderTrigger().equals(Order.OrderTrigger.IMMEDIATE)
        ) {
            if (ordersFacade.canPlaceOrder(order) && !strategyFacade.isLocked(order.getStrategy())) {
                order.setWasTraded(true);
                strategyFacade.setLocked(true);
                processMediator.placeOrder(order);
                ordersFacade.trackOpenOrder(order);
            }
        }
    }
}

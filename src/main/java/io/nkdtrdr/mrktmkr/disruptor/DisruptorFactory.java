package io.nkdtrdr.mrktmkr.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.nkdtrdr.mrktmkr.ProcessMediator;
import io.nkdtrdr.mrktmkr.account.processors.AccountReceivedProcessor;
import io.nkdtrdr.mrktmkr.account.processors.AccountUpdatedProcessor;
import io.nkdtrdr.mrktmkr.account.processors.UserListenKeyReceivedProcessor;
import io.nkdtrdr.mrktmkr.analysis.processors.CandleStickInitialisedProcessor;
import io.nkdtrdr.mrktmkr.analysis.processors.CandleStickReceivedProcessor;
import io.nkdtrdr.mrktmkr.analysis.processors.KDValueUpdatedProcessor;
import io.nkdtrdr.mrktmkr.disruptor.eventhandlers.HasThrownEventHandler;
import io.nkdtrdr.mrktmkr.disruptor.eventhandlers.ProcessorEventHandler;
import io.nkdtrdr.mrktmkr.disruptor.eventhandlers.PublisherHandler;
import io.nkdtrdr.mrktmkr.orders.processors.CancelOrderEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.ImmediateTriggerOrderRequestedEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.NewOrderPlacedEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.PriceTriggerOrderRequestedEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.TickerEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.TriggeredOrderCancelledEventProcessor;
import io.nkdtrdr.mrktmkr.orders.processors.TriggeredOrderCompleteEventProcessor;
import io.nkdtrdr.mrktmkr.persistence.processors.PersistedOrderFilledProcessor;
import io.nkdtrdr.mrktmkr.persistence.processors.PersistenceOrderRequestedHandler;
import io.nkdtrdr.mrktmkr.strategy.processors.KdValuesInitialisedProcessor;
import io.nkdtrdr.mrktmkr.strategy.processors.StrategyEventProcessor;
import io.nkdtrdr.mrktmkr.strategy.processors.InitialOrderCancelledEventProcessor;
import io.nkdtrdr.mrktmkr.triggers.processors.OrderUpdateEventProcessor;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.slf4j.LoggerFactory.getLogger;


@Configuration
public class DisruptorFactory {
    public static final int RING_BUFFER_SIZE = 128;
    private static final Logger LOGGER = getLogger(DisruptorFactory.class);

    private static final Set<String> EXCLUDED = new HashSet<>(Set.of("ORDER_REQUESTED", "CANDLE_STICK_UPDATED",
            "TICKER_UPDATED", "USER_ACCOUNT_POSITION_UPDATED", /*"KD_VALUE_UPDATED", "STRATEGY_TRIGGERED",*/
            "ACCOUNT_RECEIVED", "CANDLE_STICK_INITIALISED", "USER_LISTEN_KEY_REFRESHED"
            ));
    private final EventHandler<MakerEvent> loggerHandler = (makerEvent, l, b) -> {
        final String eventName = makerEvent.getEventEnvelope().getEventName();
        if (!EXCLUDED.contains(eventName)) {
            LOGGER.debug("{} {}", eventName,
                    makerEvent.getEventEnvelope().getPayload());
        }
    };

    @Bean
    public Disruptor<MakerEvent> getDisruptor(ProcessMediator processMediator,
                                              AccountReceivedProcessor accountReceivedProcessor,
                                              AccountUpdatedProcessor accountUpdatedProcessor,
                                              UserListenKeyReceivedProcessor userListenKeyReceivedProcessor,
                                              CandleStickInitialisedProcessor candleStickInitialisedProcessor,
                                              CandleStickReceivedProcessor candleStickReceivedProcessor,
                                              PublisherHandler publisherHandler,
                                              KDValueUpdatedProcessor kdValueUpdatedProcessor,
                                              StrategyEventProcessor strategyEventProcessor,
                                              ImmediateTriggerOrderRequestedEventProcessor immediateTriggerOrderRequestedEventProcessor,
                                              NewOrderPlacedEventProcessor newOrderPlacedEventProcessor,
                                              PriceTriggerOrderRequestedEventProcessor priceTriggerOrderRequestedEventProcessor,
                                              TickerEventProcessor tickerEventProcessor,
                                              TriggeredOrderCancelledEventProcessor triggeredOrderCancelledEventProcessor,
                                              TriggeredOrderCompleteEventProcessor triggeredOrderCompleteEventProcessor,
                                              InitialOrderCancelledEventProcessor initialOrderCancelledEventProcessor,
                                              CancelOrderEventProcessor cancelOrderEventProcessor,
                                              OrderUpdateEventProcessor orderUpdateEventProcessor,
                                              KdValuesInitialisedProcessor kdValuesInitialisedProcessor,
                                              PersistedOrderFilledProcessor persistedOrderFilledProcessor,
                                              PersistenceOrderRequestedHandler persistenceOrderRequestedHandler
    ) {
        Disruptor<MakerEvent> disruptor =
                new Disruptor<>(
                        MakerEvent::new,
                        RING_BUFFER_SIZE,
                        DaemonThreadFactory.INSTANCE);
        disruptor
                .handleEventsWith(loggerHandler)
                .then(
                        getHasThrownEventHandler(accountReceivedProcessor),
                        getHasThrownEventHandler(accountUpdatedProcessor))
                .then(getHasThrownEventHandler(cancelOrderEventProcessor))
                .then(getHasThrownEventHandler(userListenKeyReceivedProcessor))
                .then(getHasThrownEventHandler(kdValueUpdatedProcessor),
                        getHasThrownEventHandler(kdValuesInitialisedProcessor))
                .then(getHasThrownEventHandler(candleStickInitialisedProcessor),
                        getHasThrownEventHandler(candleStickReceivedProcessor))
                .then(getHasThrownEventHandler(strategyEventProcessor))
                .then(getHasThrownEventHandler(immediateTriggerOrderRequestedEventProcessor))
                .then(getHasThrownEventHandler(newOrderPlacedEventProcessor))
                .then(getHasThrownEventHandler(priceTriggerOrderRequestedEventProcessor),
                        getHasThrownEventHandler(persistenceOrderRequestedHandler))
                .then(getHasThrownEventHandler(tickerEventProcessor))
                .then(getHasThrownEventHandler(triggeredOrderCancelledEventProcessor))
                .then(getHasThrownEventHandler(triggeredOrderCompleteEventProcessor),
                        getHasThrownEventHandler(persistedOrderFilledProcessor))
                .then(
                        getHasThrownEventHandler(initialOrderCancelledEventProcessor)
                        )
                .then(getHasThrownEventHandler(orderUpdateEventProcessor))
                .then(publisherHandler)
                .then(((makerEvent, l, b) -> {
                    if (makerEvent.hasThrown()) {
                        final Throwable throwable = makerEvent.getThrowable();
                        LOGGER.info("Message {} failed {}", l, getRootCauseMessage(throwable), throwable);
                    }
                }))
                .then((makerEvent, l, b) -> makerEvent.clear());
        processMediator.setDisruptor(disruptor);
        disruptor.start();
        return disruptor;
    }

    private HasThrownEventHandler getHasThrownEventHandler(EventProcessor accountReceivedProcessor) {
        return new HasThrownEventHandler(new ProcessorEventHandler(accountReceivedProcessor));
    }
}

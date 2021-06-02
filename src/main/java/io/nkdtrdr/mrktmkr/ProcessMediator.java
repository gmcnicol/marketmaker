package io.nkdtrdr.mrktmkr;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.google.common.collect.ImmutableMap;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.dsl.Disruptor;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.dto.CandleStickDTO;
import io.nkdtrdr.mrktmkr.dto.CandleStickInitialisedEvent;
import io.nkdtrdr.mrktmkr.dto.Order;
import io.nkdtrdr.mrktmkr.orderbook.OrderBookRepository;
import io.nkdtrdr.mrktmkr.orders.BinanceOrderFactory;
import io.nkdtrdr.mrktmkr.orders.OrdersFacade;
import io.nkdtrdr.mrktmkr.persistence.PersistenceFacade;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import static io.nkdtrdr.mrktmkr.disruptor.EventEnvelope.EventEnvelopeBuilder.anEventEnvelope;
import static io.nkdtrdr.mrktmkr.dto.CandleStickInitialisedEvent.CandleStickInitialisedEventBuilder.aCandleStickInitialisedEvent;
import static org.slf4j.LoggerFactory.getLogger;


@Component
public class ProcessMediator {
    private static final Logger LOGGER = getLogger(ProcessMediator.class);

    static EventTranslatorTwoArg<MakerEvent, String, Object> eventTranslator = (makerEvent, l, eventName, o) -> {
        makerEvent.setEventName(eventName);
        final EventEnvelope.EventEnvelopeBuilder envelopeBuilder =
                anEventEnvelope()
                        .withEventName(eventName)
                        .withPayload(o);
        makerEvent.setEventEnvelope(envelopeBuilder.build());
    };
    private final RestClientAdapter restClient;
    private final WebsocketAdapter websocketAdapter;
    private final OrderBookRepository orderBookRepository;
    private final ModelMapper modelMapper;
    private final PersistenceFacade persistenceFacade;
    private final OrdersFacade ordersFacade;
    private final Symbol symbol;
    private Disruptor<MakerEvent> disruptor;
    private String listenKey;

    public ProcessMediator(RestClientAdapter restClient,
                           WebsocketAdapter websocketAdapter,
                           OrderBookRepository orderBookRepository,
                           ModelMapper modelMapper, final PersistenceFacade persistenceFacade,
                           @Lazy OrdersFacade ordersFacade, final Symbol symbol) {
        this.restClient = restClient;
        this.modelMapper = modelMapper;
        this.persistenceFacade = persistenceFacade;
        this.ordersFacade = ordersFacade;
        this.symbol = symbol;
        this.restClient.setProcessMediator(this);

        this.websocketAdapter = websocketAdapter;
        this.websocketAdapter.setProcessMediator(this);

        this.orderBookRepository = orderBookRepository;
        this.orderBookRepository.setProcessMediator(this);
    }

    public void start() {
        LOGGER.info("Initialising");
        persistenceFacade.getAllOrders().forEach(ordersFacade::addOrderToBeTriggered);
        restClient.startUserStream();
        restClient.getAccount();
        //restClient.getCandleStick(symbol.getSymbol().toUpperCase(Locale.ROOT), CandlestickInterval.FIVE_MINUTES);
        restClient.getCandleStick(symbol.getSymbol().toUpperCase(), CandlestickInterval.ONE_MINUTE);
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Stopping");
        restClient.cancelAllOrders(symbol.getSymbol());
        websocketAdapter.stop();
    }

    public void getOrderBookForSymbol(String symbol) {
        restClient.getOrderBook(symbol, 10);
    }

    public void processDepthEvent(String symbol, DepthEvent payload) {
        orderBookRepository
                .getForSymbol(symbol).processDepthEvent(payload);
    }

    public void setOrderBook(String symbol, OrderBook orderBook) {
        orderBookRepository.getForSymbol(symbol).setOrderBook(orderBook);
    }

    public void setDisruptor(Disruptor<MakerEvent> disruptor) {
        this.disruptor = disruptor;
    }

    public void setListenKey(String listenKey) {
        this.listenKey = listenKey;
        websocketAdapter.startUserFeed(listenKey);
        websocketAdapter.startMarketFeed(symbol.getSymbol().toLowerCase(Locale.ROOT));
    }

    @Scheduled(cron = "25 7/30 * * * *")
    public void refreshUserFeed() {
        restClient.refreshUserStream(this.listenKey);
    }

    public void processKeyEvent(String eventName, String eventKey, Object payload) {
        final ImmutableMap<String, Object> params = ImmutableMap.of("key", eventKey, "payload", payload);
        disruptor.publishEvent(eventTranslator, eventName, params);
    }

    public void onCandlestickInitialised(String symbol,
                                         CandlestickInterval interval,
                                         Collection<Candlestick> candlesticks
    ) {
        final Collection<CandleStickDTO> collect = candlesticks.stream().
                map(candlestick -> modelMapper.map(candlestick, CandleStickDTO.class))
                .peek(dto -> dto.setIntervalId(interval.getIntervalId()))
                .peek(dto -> dto.setSymbol(symbol))
                .peek(dto -> dto.setBarFinal(true))
                .collect(Collectors.toList());

        final CandleStickInitialisedEvent candleStickInitialisedEvent = aCandleStickInitialisedEvent()
                .withCandleStickDTOS(collect)
                .withInterval(interval.getIntervalId())
                .withSymbol(symbol)
                .build();

        disruptor.publishEvent(eventTranslator,
                "CANDLE_STICK_INITIALISED",
                candleStickInitialisedEvent
        );
    }

    public void onCandleStickUpdated(CandlestickEvent candlestickEvent) {
        CandleStickDTO candleStickDTO = modelMapper.map(candlestickEvent, CandleStickDTO.class);
        this.processEvent("CANDLE_STICK_UPDATED", candleStickDTO);
    }

    public void processEvent(String eventName, Object payload) {
        disruptor.publishEvent(eventTranslator, eventName, payload);
    }

    public void startCandleStickStream(CandleStickInitialisedEvent payload) {
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        for (CandlestickInterval value : CandlestickInterval.values()) {
            if (value.getIntervalId().equals(payload.getInterval())) {
                interval = value;
                break;
            }
        }
        websocketAdapter.startCandleStickStream(payload.getSymbol(), interval);
    }

    public void onAccountReceived(Account account) {
        this.processEvent("ACCOUNT_RECEIVED", account);
    }

    public void placeOrder(final Order order) {
        if (order.getPrice().multiply(order.getQuantity()).compareTo(BigDecimal.TEN) < 0) {
            return;
        }
        if (order.getSide().equals(Order.OrderSide.BUY))
            placeBuyOrder(order);

        if (order.getSide().equals(Order.OrderSide.SELL))
            placeSellOrder(order);
    }

    private void placeBuyOrder(final Order order) {

        final NewOrder newOrder = BinanceOrderFactory.limitBuy(order.getSymbol(),
                order.getPrice(),
                order.getQuantity().setScale(6, RoundingMode.FLOOR))
                .newClientOrderId(order.getOrderId());
        placeOrder(newOrder);
    }

    private void placeSellOrder(final Order order) {

        final NewOrder newOrder = BinanceOrderFactory.limitSell(order.getSymbol(),
                order.getPrice(),
                order.getQuantity().setScale(6, RoundingMode.FLOOR))
                .newClientOrderId(order.getOrderId());
        placeOrder(newOrder);
    }

    private void placeOrder(NewOrder newOrder) {
        restClient.placeOrder(newOrder);
    }

    public void cancelOrder(final String orderId, final String symbol) {
        restClient.cancelOrder(symbol, orderId);
    }
}

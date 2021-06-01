package io.nkdtrdr.mrktmkr;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Component
public class WebsocketAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketAdapter.class);

    private final BinanceApiWebSocketClient binanceApiWebSocketClient;
    private final Set<Closeable> closable;

    private ProcessMediator processMediator;

    public WebsocketAdapter(BinanceApiWebSocketClient binanceApiWebSocketClient) {
        this.binanceApiWebSocketClient = binanceApiWebSocketClient;
        closable = new HashSet<>();
    }

    private static boolean isAccountUpdate(UserDataUpdateEvent userDataUpdateEvent) {
        return UserDataUpdateEvent.UserDataUpdateEventType.ACCOUNT_POSITION_UPDATE
                == userDataUpdateEvent.getEventType();
    }

    private static boolean eventIsOrderUpdate(UserDataUpdateEvent userDataUpdateEvent) {
        return userDataUpdateEvent.getEventType() ==
                UserDataUpdateEvent.UserDataUpdateEventType.ORDER_TRADE_UPDATE;
    }

    public void setProcessMediator(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    public void startDepthEvent(String symbol) {
        final Closeable depthEvent = binanceApiWebSocketClient.onDepthEvent(symbol.toLowerCase(),
                this::depthEventCallback);
        closable.add(depthEvent);
    }

    private void depthEventCallback(DepthEvent depthEvent) {
        processMediator.processEvent("DEPTH_EVENT_RECEIVED", depthEvent);
    }

    public void startMarketFeed(String symbol) {
        final Closeable tickerUpdated = binanceApiWebSocketClient.onAllMarketTickersEvent(tickers -> {
            tickers.stream().filter(e -> symbol.toUpperCase(Locale.ROOT).equals(e.getSymbol()))
                    .forEach(t -> processMediator.processKeyEvent("TICKER_UPDATED", symbol, t));
        });
        closable.add(tickerUpdated);
    }

    public void stop() {
        for (Closeable c : closable) {
            try {
                c.close();
            } catch (IOException e) {
                LOGGER.warn("Couldn't close {}", getRootCauseMessage(e), e);
            }
        }
    }

    public void startUserFeed(String listenKey) {
        closable.add(binanceApiWebSocketClient.onUserDataUpdateEvent(listenKey,
                this::onUserUpdateEvent));
    }

    private void onUserUpdateEvent(UserDataUpdateEvent userDataUpdateEvent) {
        if (isAccountUpdate(userDataUpdateEvent)) {
            processMediator.processEvent("USER_ACCOUNT_POSITION_UPDATED",
                    userDataUpdateEvent.getAccountUpdateEvent());
        }

        if (eventIsOrderUpdate(userDataUpdateEvent))
            processMediator.processEvent("USER_TRADE_UPDATED", userDataUpdateEvent.getOrderTradeUpdateEvent());
    }

    public void startCandleStickStream(String symbol, CandlestickInterval candlestickInterval) {
        final Closeable candleStickUpdated = binanceApiWebSocketClient
                .onCandlestickEvent(symbol.toLowerCase(),
                        candlestickInterval,
                        processMediator::onCandleStickUpdated);
        closable.add(candleStickUpdated);
    }
}

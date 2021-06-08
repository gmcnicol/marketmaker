package io.nkdtrdr.mrktmkr;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import io.nkdtrdr.mrktmkr.symbols.Symbol;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;


@Component
public class RestClientAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientAdapter.class);
    private final BinanceApiAsyncRestClient restClient;
    private final BinanceApiRestClient syncClient;
    private ProcessMediator processMediator;

    public RestClientAdapter(BinanceApiAsyncRestClient restClient, BinanceApiRestClient syncClient) {
        this.restClient = restClient;
        this.syncClient = syncClient;
    }

    public void getOrderBook(String symbol, Integer limit) {
        restClient.getOrderBook(symbol.toUpperCase(),
                limit,
                orderBook -> onOrderBookReceived(symbol.toUpperCase(), orderBook));
    }

    private void onOrderBookReceived(String symbol, OrderBook orderBook) {
        Pair<String, OrderBook> payload = Pair.of(symbol, orderBook);
        processMediator.processEvent("ORDER_BOOK_RECEIVED", payload);
    }

    public void getAccount() {
        restClient.getAccount(processMediator::onAccountReceived);
    }

    public void setProcessMediator(ProcessMediator processMediator) {
        this.processMediator = processMediator;
    }

    public void startUserStream() {
        restClient.startUserDataStream(listenKey ->
                processMediator.processEvent("USER_LISTEN_KEY_RECEIVED", listenKey));
    }

    public void refreshUserStream(String listenKey) {
        restClient.keepAliveUserDataStream(listenKey, unused ->
                processMediator.processEvent("USER_LISTEN_KEY_REFRESHED", listenKey));
    }

    public void placeOrder(NewOrder newOrder) {
        try {
            restClient.newOrder(newOrder,
                    newOrderResponse -> processMediator.processEvent("NEW_ORDER_PLACED", newOrderResponse));
        } catch (Exception e) {
            LOGGER.warn("NEW ORDER {} FAIL  {} ", newOrder.getNewClientOrderId(), getRootCauseMessage(e));
        }
    }

    public void cancelOrder(String symbol, String orderId) {
        try {
            final CancelOrderResponse cancelOrderResponse =
                    syncClient.cancelOrder(new CancelOrderRequest(symbol.toUpperCase(
                            Locale.ROOT), orderId));
            processMediator.processEvent("ORDER_CANCELLED", cancelOrderResponse);
        } catch (Exception e) {
            //   LOGGER.warn("CANCEL ORDER {} FAIL {} ", orderId, getRootCauseMessage(e));
        }
    }

    public void cancelAllOrders(String symbol) {
        final List<Order> orders = syncClient.getOpenOrders(new OrderRequest(symbol.toUpperCase(Locale.ROOT)));
        orders.forEach(o -> syncClient.cancelOrder(new CancelOrderRequest(o.getSymbol(), o.getOrderId())));
    }

    public void getCandleStick(String symbol, CandlestickInterval interval) {
        restClient.getCandlestickBars(symbol.toUpperCase(),
                interval,
                candlesticks -> processMediator.onCandlestickInitialised(
                        symbol,
                        interval,
                        candlesticks));
    }

    public void getSymbolStats(Symbol symbol){
        restClient.get24HrPriceStatistics(symbol.getSymbol(),
                tickerStatistics -> processMediator.processEvent("SYMBOL_STATS_UPDATED", tickerStatistics));
    }

}

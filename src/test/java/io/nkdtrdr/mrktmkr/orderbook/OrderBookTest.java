package io.nkdtrdr.mrktmkr.orderbook;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OrderBookTest {

    private OrderBookCache orderBookCache;

    @BeforeEach
    void setUp(@Mock ProcessMediator processMediator) {
        orderBookCache = new OrderBookCache(processMediator);
    }

    @Test
    @DisplayName("Can create OrderBook")
    void canBuild() {
        assertNotNull(orderBookCache, "OrderBook was not created");
        assertTrue(orderBookCache.getAsks().isEmpty());
        assertTrue(orderBookCache.getBids().isEmpty());
    }
}

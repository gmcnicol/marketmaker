package io.nkdtrdr.mrktmkr.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class OrderBookRepositoryTest {
    public static final String SYMBOL = "symbol";
    OrderBookRepository repository;

    @BeforeEach
    void setUp(@Mock OrderBookCacheFactory factory, @Mock OrderBookCache cache) {

        lenient().when(factory.build()).thenReturn(cache);
        repository = new OrderBookRepository(factory);
    }

    @Test
    void canBuild() {
        assertNotNull(repository);
    }

    @Test
    void getSymbolReturnsNew() {
        final OrderBookCache forSymbol = repository.getForSymbol(SYMBOL);
        assertNotNull(forSymbol);
    }

    @Test
    void getTwiceReturnsSame() {
        final OrderBookCache forSymbol = repository.getForSymbol(SYMBOL);
        final OrderBookCache forSymbol1 = repository.getForSymbol(SYMBOL);
        assertSame(forSymbol, forSymbol1);
    }
}

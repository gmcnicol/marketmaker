package io.nkdtrdr.mrktmkr.orderbook;

import io.nkdtrdr.mrktmkr.ProcessMediator;
import org.springframework.stereotype.Component;

@Component
public class OrderBookCacheFactory {
    private ProcessMediator mediator;

    public OrderBookCacheFactory() {

    }

    OrderBookCache build() {
        return new OrderBookCache(mediator);
    }

    public void setMediator(ProcessMediator mediator) {
        this.mediator = mediator;
    }
}

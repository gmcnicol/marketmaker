package io.nkdtrdr.mrktmkr.audit;

import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

@Component
public class TradeAudit {

    public TradeAudit() {
    }

    public void auditOrder(OrderTradeUpdateEvent event) {
        final String log = String.format("%d %s %s %s %s %s %n",
                event.getEventTime(),
                event.getSide(),
                event.getPriceOfLastFilledTrade(),
                event.getQuantityLastFilledTrade(),
                event.getLastQuoteQty(),
                event.getNewClientOrderId());
        try {
            Files.write(Paths.get("logs/marketmaker.log"), log.getBytes(StandardCharsets.UTF_8),
                    CREATE, WRITE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package io.nkdtrdr.mrktmkr.account.processors;

import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.AccountUpdateEvent;
import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import io.nkdtrdr.mrktmkr.persistence.PersistenceFacade;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;


@Component
public class AccountUpdatedProcessor implements EventProcessor {
    private final AccountFacade accountFacade;
    private final PersistenceFacade persistenceFacade;

    public AccountUpdatedProcessor(AccountFacade accountFacade, final PersistenceFacade persistenceFacade) {
        this.accountFacade = accountFacade;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "USER_ACCOUNT_POSITION_UPDATED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        AccountUpdateEvent accountUpdateEvent = (AccountUpdateEvent) makerEvent.getEventEnvelope().getPayload();
        final List<AssetBalance> balances = accountUpdateEvent.getBalances();
        accountFacade.setAccountBalances(balances);
        persistenceFacade.saveAssets(balances);
    }
}

package io.nkdtrdr.mrktmkr.account.processors;

import com.binance.api.client.domain.account.Account;
import io.nkdtrdr.mrktmkr.account.AccountFacade;
import io.nkdtrdr.mrktmkr.disruptor.EventEnvelope;
import io.nkdtrdr.mrktmkr.disruptor.EventProcessor;
import io.nkdtrdr.mrktmkr.disruptor.MakerEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AccountReceivedProcessor implements EventProcessor {
    private final AccountFacade accountFacade;

    public AccountReceivedProcessor(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @Override
    public boolean shouldProcessEventName(String eventName) {
        return "ACCOUNT_RECEIVED".equals(eventName);
    }

    @Override
    public void process(MakerEvent makerEvent, Consumer<EventEnvelope> resultHandler) {
        final Account account = (Account) makerEvent.getEventEnvelope().getPayload();
        accountFacade.initialiseAccount(account);
    }
}

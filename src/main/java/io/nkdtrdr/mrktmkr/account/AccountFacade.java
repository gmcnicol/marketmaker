package io.nkdtrdr.mrktmkr.account;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.math.BigDecimal.valueOf;


@Component
public class AccountFacade {
    public static final BigDecimal VALUE_CAP = valueOf(50L);
    private final ConcurrentHashMap<String, Function<BigDecimal, BigDecimal>> adjusters = new ConcurrentHashMap<>(2);
    private final AccountMediator accountMediator;

    public AccountFacade(AccountMediator accountMediator) {
        this.accountMediator = accountMediator;
    }

    public void initialiseAccount(Account account) {
        accountMediator.initialiseAccount(account);
    }

    public BigDecimal getMakerCommission() {
        return accountMediator.getMakerCommission();
    }

    public BigDecimal getTakerCommission() {
        return accountMediator.getTakerCommission();
    }

    public void setAccountBalances(List<AssetBalance> accountBalance) {
        accountMediator.setAccountBalances(accountBalance);
    }

    public BigDecimal getFreeBalanceForAsset(String asset) {
        final BigDecimal freeBalanceForAsset = accountMediator.getFreeBalanceForAsset(asset);
        if (adjusters.containsKey(asset)) {
            return adjusters.get(asset).apply(freeBalanceForAsset);
        }
        return freeBalanceForAsset;
    }
}

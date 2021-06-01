package io.nkdtrdr.mrktmkr.account;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
class AccountMediator {

    private final AccountBalanceCache accountBalanceCache;
    private final CommissionRates commissionRates;

    AccountMediator(AccountBalanceCache accountBalanceCache,
                    CommissionRates commissionRates) {
        this.accountBalanceCache = accountBalanceCache;
        this.commissionRates = commissionRates;
    }

    void initialiseAccount(Account account) {
        accountBalanceCache.setAccountBalance(account.getBalances());
        commissionRates.setMakerCommissionBip(account.getMakerCommission());
        commissionRates.setTakerCommissionBip(account.getTakerCommission());
    }

    public BigDecimal getMakerCommission() {
        return commissionRates.getMakerCommission();
    }

    public BigDecimal getTakerCommission() {
        return commissionRates.getTakerCommission();
    }

    public void setAccountBalances(List<AssetBalance> accountBalance) {
        accountBalanceCache.setAccountBalance(accountBalance);
    }

    public BigDecimal getFreeBalanceForAsset(String asset) {
        return accountBalanceCache.getFreeBalanceForAsset(asset);
    }

    public BigDecimal getTotalBalanceForAsset(String asset) {
        return accountBalanceCache.getTotalBalanceForAsset(asset);
    }
}

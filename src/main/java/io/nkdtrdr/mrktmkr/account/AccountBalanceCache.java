package io.nkdtrdr.mrktmkr.account;

import com.binance.api.client.domain.account.AssetBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;


@Component
class AccountBalanceCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceCache.class);

    private final ConcurrentHashMap<String, DecimalAsset> accountBalanceCache;

    public AccountBalanceCache() {
        accountBalanceCache = new ConcurrentHashMap<>();
    }

    public void setAccountBalance(List<AssetBalance> accountBalance) {
        final List<DecimalAsset> decimalAssets = accountBalance.stream().map(DecimalAsset::new)
                .filter(decimalAsset -> ZERO.compareTo(
                        decimalAsset.getTotal()) != 0)
                .collect(Collectors.toList());

        decimalAssets.forEach(assetBalance -> accountBalanceCache.put(assetBalance.getAsset(), assetBalance));
        LOGGER.info("{}", this);
    }

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00000000");
        accountBalanceCache.forEach((s, decimalAsset) ->
                result.append(
                        format("a:%s(f:%s l:%s) ",
                                s,
                                decimalFormat.format(decimalAsset.free),
                                decimalFormat.format(decimalAsset.locked)
                        )
                ));
        return result.toString();
    }

    public BigDecimal getFreeBalanceForAsset(String asset) {
        return accountBalanceCache.containsKey(asset)
                ? accountBalanceCache.get(asset).getFree()
                : ZERO;
    }

    public BigDecimal getTotalBalanceForAsset(String asset) {
        return accountBalanceCache.containsKey(asset) ? accountBalanceCache.get(asset).getTotal() : ZERO;
    }

    public static final class DecimalAsset {
        private final BigDecimal free;
        private final BigDecimal locked;
        private final String asset;

        DecimalAsset(AssetBalance assetBalance) {
            free = new BigDecimal(assetBalance.getFree());
            locked = new BigDecimal(assetBalance.getLocked());
            asset = assetBalance.getAsset();
        }

        public BigDecimal getFree() {
            return free;
        }

        public BigDecimal getLocked() {
            return locked;
        }

        public String getAsset() {
            return asset;
        }

        public BigDecimal getTotal() {
            return free.add(locked);
        }
    }
}

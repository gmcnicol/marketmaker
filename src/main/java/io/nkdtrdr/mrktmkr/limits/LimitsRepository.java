package io.nkdtrdr.mrktmkr.limits;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@ConfigurationProperties(prefix = "strategy")
public class LimitsRepository {

    private Set<Limit> limits;

    public Set<Limit> getLimits() {
        return limits;
    }

    public void setLimits(final Set<Limit> limits) {
        this.limits = limits;
    }

    public Limit getLimitForAssetAndStrategy(String asset, String strategy) {
        return limits.stream().filter(limit -> limit.getAsset().equals(asset))
                .filter(limit -> limit.getStrategy().equals(strategy))
                .findFirst().orElse(null);
    }
}

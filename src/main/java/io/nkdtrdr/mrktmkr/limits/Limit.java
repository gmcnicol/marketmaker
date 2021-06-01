package io.nkdtrdr.mrktmkr.limits;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;


public class Limit {
    private String asset;
    private String strategy;
    private BigDecimal assetCap;

    public String getAsset() {
        return asset;
    }

    public void setAsset(final String asset) {
        this.asset = asset;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(final String strategy) {
        this.strategy = strategy;
    }

    public BigDecimal getAssetCap() {
        return assetCap;
    }

    public void setAssetCap(final BigDecimal assetCap) {
        this.assetCap = assetCap;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Limit limit = (Limit) o;
        return Objects.equal(getAsset(), limit.getAsset()) && Objects.equal(getStrategy(), limit.getStrategy()) && Objects.equal(getAssetCap(), limit.getAssetCap());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAsset(), getStrategy(), getAssetCap());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(asset)
                .addValue(strategy)
                .add("cap", assetCap)
                .toString();
    }
}

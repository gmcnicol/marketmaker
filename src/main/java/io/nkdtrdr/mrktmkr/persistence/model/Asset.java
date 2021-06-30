package io.nkdtrdr.mrktmkr.persistence.model;

import com.binance.api.client.domain.account.AssetBalance;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity(name = "asset")
public class Asset {
    private String name;
    private BigDecimal value;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long id;

    public static Asset convert(AssetBalance assetBalance) {
        final Asset result = new Asset();
        result.setName(assetBalance.getAsset());
        result.setValue(new BigDecimal(assetBalance.getFree()));
        return result;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

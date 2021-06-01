package io.nkdtrdr.mrktmkr.account;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CommissionRates {
    private BigDecimal makerCommission;
    private BigDecimal takerCommission;

    public void setMakerCommissionBip(int makerCommissionBip) {
        this.makerCommission = new BigDecimal(makerCommissionBip).movePointLeft(4);
    }

    public BigDecimal getMakerCommission() {
        return makerCommission;
    }

    public void setTakerCommissionBip(int takerCommissionBip) {
        takerCommission = new BigDecimal(takerCommissionBip).movePointLeft(4);
    }

    public BigDecimal getTakerCommission() {
        return takerCommission;
    }
}

package com.walletservice.walletservice.dtos;

import java.math.BigDecimal;

public class CreditResponse {
    private BigDecimal credit_amount;
    private BigDecimal total_amount;

    public BigDecimal getCredit_amount() {
        return credit_amount;
    }

    public void setCredit_amount(BigDecimal credit_amount) {
        this.credit_amount = credit_amount;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }
}

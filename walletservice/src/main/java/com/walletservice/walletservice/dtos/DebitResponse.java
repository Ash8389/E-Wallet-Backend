package com.walletservice.walletservice.dtos;

import java.math.BigDecimal;

public class DebitResponse {
    private BigDecimal debit_amount;
    private BigDecimal total_amount;

    public BigDecimal getDebit_amount() {
        return debit_amount;
    }

    public void setDebit_amount(BigDecimal debit_amount) {
        this.debit_amount = debit_amount;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }
}

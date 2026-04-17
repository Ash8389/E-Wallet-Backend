package com.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionCompleteEvent {

    private final Long transactionId;
    private final Long senderId;
    private final Long receiverId;
    private final BigDecimal amount;
    private final Status status;
    private final LocalDateTime completedAt;

    public TransactionCompleteEvent(Long transactionId,
                                    Long senderId,
                                    Long receiverId,
                                    BigDecimal amount,
                                    Status status,
                                    LocalDateTime completedAt) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.completedAt = completedAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}

package com.transactionservice.transactionservice.service;

import com.common.dto.Status;
import com.common.dto.TransactionCompleteEvent;
import com.common.dto.TransactionEvent;
import com.transactionservice.transactionservice.model.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionCompleteEvent> kafkaTemplate;

    KafkaProducerService(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceEvent(Transaction transaction){

        TransactionCompleteEvent completeEvent = new TransactionCompleteEvent(
            transaction.getId(),
            transaction.getSenderId(),
            transaction.getReceiverId(),
            transaction.getAmount(),
            transaction.getStatus(),
            transaction.getSendAt()
        );

        kafkaTemplate.send("transaction-topic", completeEvent);
    }
}

package com.transactionservice.transactionservice.service;

import com.common.dto.Status;
import com.common.dto.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    KafkaProducerService(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceEvent(Status st){

        System.out.println("Produced : " + st.name());

        kafkaTemplate.send("transaction-topic", "Transaction " + st.name());
    }
}

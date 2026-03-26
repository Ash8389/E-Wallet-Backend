//package com.walletservice.walletservice.service;
//
//import com.common.dto.Status;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import com.common.dto.TransactionEvent;
//
//@Service
//public class KafkaConsumerService {
//
//    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
//    private final WalletService walletService;
//
//    KafkaConsumerService(WalletService walletService, KafkaTemplate<String, TransactionEvent> kafkaTemplate){
//        this.walletService = walletService;
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @KafkaListener(topics = "transaction-topic", groupId = "wallet-group")
//    public void consume(TransactionEvent event){
//
//        System.out.println("Consumed : " + event);
//
//        try{
//            walletService.debit(event.getSenderWalletId(), event.getAmount());
//            walletService.credit(event.getReceiverWalletId(), event.getAmount());
//
//            event.setStatus(Status.DONE);
//        }catch (Exception e){
//
//            try {
//                walletService.credit(event.getSenderWalletId(), event.getAmount());
//            }catch (Exception ex){
//                System.out.println("Compensation failed");
//            }
//
//            event.setStatus(Status.FAILED);
//        }
//
//        kafkaTemplate.send("transaction-topic-result", event);
//    }
//}

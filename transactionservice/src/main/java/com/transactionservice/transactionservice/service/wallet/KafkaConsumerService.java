//package com.transactionservice.transactionservice.service.wallet;
//
//import com.transactionservice.transactionservice.dto.TransactionEvent;
//import com.transactionservice.transactionservice.model.Status;
//import org.springframework.kafka.annotation.KafkaListener;
//
//public class KafkaConsumerService {
//
//    WalletClientService walletClientService;
//
//
//    KafkaConsumerService(WalletClientService walletClientService) {
//        this.walletClientService = walletClientService;
//    }
//
//    @KafkaListener(topics = "transaction-topic", groupId = "wallet-group")
//    public void consume(TransactionEvent event){
//        try{
//            walletClientService.debitService(event.getSenderWalletId(), event.getAmount());
//            walletClientService.creditService(event.getReceiverWalletId(), event.getAmount());
//
//            event.setStatus(Status.DONE);
//        }catch (Exception e){
//
//            try {
//                walletClientService.creditService(event.getSenderWalletId(), event.getAmount());
//            }catch (Exception ex){
//                System.out.println("Compensation failed");
//            }
//
//            event.setStatus(Status.FAILED);
//        }
//    }
//}

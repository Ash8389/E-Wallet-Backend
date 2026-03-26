package com.transactionservice.transactionservice.service.wallet;

import com.common.dto.Status;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WalletClientService {

    private RestTemplate restTemplate;

    public WalletClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "fallbackTransaction")
    @Retry(name = "walletService")
    public Status transferService(Long senderId, Long receiverId, Double amount, Long transactionId){
        return restTemplate.postForObject(
                "http://localhost:8082/wallet/transfer",
                Map.of(
                        "senderId", senderId,
                        "receiverId" ,receiverId,
                        "amount" , amount,
                        "transactionId", transactionId
                ),
                Status.class
        );
    }

    public Status fallbackTransaction(Long senderId, Long receiverId, Double amount, Long transactionId, Exception ex){
        System.out.println("🔥 FALLBACK CALLED: " + ex.getMessage());

        return Status.FAILED;
    }
}

package com.walletservice.walletservice.exception;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(){
        super("Insufficient wallet balance");
    }
}

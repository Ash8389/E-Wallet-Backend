package com.userservice.userservice.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String error;
    private String msg;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status, String error, String msg){
        this.status = status;
        this.error = error;
        this.msg = msg;
        this.timeStamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}

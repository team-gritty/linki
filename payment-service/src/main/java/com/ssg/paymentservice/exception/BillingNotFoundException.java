package com.ssg.paymentservice.exception;

//billing 테이블에서 userId 가 존재하지 않음.
public class BillingNotFoundException extends RuntimeException {
    public BillingNotFoundException(String message) {
        super(message);
    }
}

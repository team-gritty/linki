package com.ssg.paymentservice.exception;

//Authorization 헤더가 없음
public class NoAuthorizationHeader extends RuntimeException {
    public NoAuthorizationHeader(String message) {
        super(message);
    }
}

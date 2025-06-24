package com.ssg.paymentservice.service;

public interface BillingService {
    String confirmBilling(String authKey, String customerKey);
}

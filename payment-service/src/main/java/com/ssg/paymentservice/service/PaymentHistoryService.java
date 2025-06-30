package com.ssg.paymentservice.service;

public interface PaymentHistoryService {
    Integer countByUserIdAndSuccessTrue(String userId);
}

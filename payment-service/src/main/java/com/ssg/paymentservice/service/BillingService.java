package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.dto.responsedto.BillingKeyResponseDto;

public interface BillingService {
    //빌링키 요청
    BillingKeyResponseDto confirmBilling(String authKey, String customerKey);
    String requestPaymentWithBilling(String userId);
}

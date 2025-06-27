package com.ssg.paymentservice.service;

import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.dto.BillingInfoDto;

public interface PaymentService {
    BillingDto getBillingDtoByUserId(String userId);
    BillingInfoDto getBillingInfoDto(String userId);
}

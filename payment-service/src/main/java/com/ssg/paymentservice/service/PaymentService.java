package com.ssg.paymentservice.service;

import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.dto.BillingInfoDto;

import java.util.List;

public interface PaymentService {
    BillingDto getBillingDtoByUserId(String userId);
    BillingInfoDto getBillingInfoDto(String userId);
    List<BillingDto> ListAutoBillingAtNow();
}

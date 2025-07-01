package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.toss.TossConfig;
import com.ssg.paymentservice.dto.dto.BillingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingScheduler {
    private final TossBillingService tossBillingService;
    private final PaymentService paymentService;

    // 매일 00시 00분에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runAutoBilling() {
        List<BillingDto> billingDtos = paymentService.ListAutoBillingAtNow();
        tossBillingService.processAutoBilling(billingDtos);
    }
}

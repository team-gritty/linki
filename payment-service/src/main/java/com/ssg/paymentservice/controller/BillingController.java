package com.ssg.paymentservice.controller;

import com.ssg.paymentservice.service.BillingService;
import com.ssg.paymentservice.service.TossBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/payment-service/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService tossBillingService;   // billing 키 발급 서비스

    /**
     * 토스 빌링 성공 콜백
     *
     */
    @GetMapping("/success")
    public ResponseEntity<Void> billingSuccess(
            @RequestParam("customerKey") String customerKey,
            @RequestParam("authKey")     String authKey) {

        //토스 서버에 authKey 확정 요청 (Feign)
        tossBillingService.confirmBilling(customerKey, authKey);

        //비즈니스 처리 완료 → 200 OK (본문 없음)
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/fail")
//    public ResponseEntity<Void> billingSuccess(
//            @RequestParam("customerKey") String customerKey,
//            @RequestParam("authKey")     String authKey) {
//
//        //토스 서버에 authKey 확정 요청 (Feign)
//        tossBillingService.confirmAuthorization(customerKey, authKey);
//
//        //비즈니스 처리 완료 → 200 OK (본문 없음)
//        return ResponseEntity.ok().build();
//    }
}
package com.ssg.paymentservice.controller;

import com.ssg.paymentservice.common.util.SecurityUtil;
import com.ssg.paymentservice.dto.dto.BillingInfoDto;
import com.ssg.paymentservice.dto.requestdto.AuthCardRequestDto;
import com.ssg.paymentservice.dto.responsedto.BillingInfoResponseDto;
import com.ssg.paymentservice.dto.responsedto.BillingKeyResponseDto;
import com.ssg.paymentservice.service.PaymentService;
import com.ssg.paymentservice.service.TossBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payment-service/api/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final TossBillingService tossBillingService;   // billing 키 발급 서비스
    private final SecurityUtil securityUtil;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;


    /**
     * 토스 빌링 성공 콜백
     *
     */
    @PostMapping("/success")
    public void billingSuccess(@RequestBody AuthCardRequestDto authCardRequestDto) {
        // billingKey 발급 및 저장 처리
        BillingKeyResponseDto responseDto = tossBillingService.confirmBilling(
                authCardRequestDto.getAuthKey(),
                authCardRequestDto.getCustomerKey()
        );
    }

    @GetMapping("/user/billing-info")
    public ResponseEntity<BillingInfoResponseDto> billingInfo() {
        String currentUserId = securityUtil.getCurrentUserId();
        BillingInfoDto billingInfoDto = paymentService.getBillingInfoDto(currentUserId);
        BillingInfoResponseDto billingInfoResponseDto = modelMapper.map(billingInfoDto, BillingInfoResponseDto.class);
        return ResponseEntity.ok(billingInfoResponseDto);
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
package com.ssg.paymentservice.feignClient;

import com.ssg.paymentservice.dto.requestdto.AuthCardRequestDto;
import com.ssg.paymentservice.dto.requestdto.BillingKeyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

//빌링키 발급 요청 (Toss)
@FeignClient(name = "feignGetBillingKey", url = "https://api.tosspayments.com")
public interface FeignGetBillingKey {

    @PostMapping(value = "/v1/billing/authorizations/issue", consumes = "application/json")
    ResponseEntity<BillingKeyResponseDto> getBillingKey(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AuthCardRequestDto request
    );
}

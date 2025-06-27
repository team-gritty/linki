package com.ssg.paymentservice.feignClient;

import com.ssg.paymentservice.dto.requestdto.AutoPaymentRequestDto;
import com.ssg.paymentservice.dto.responsedto.AutoPaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "feignRequestTossPayment", url = "https://api.tosspayments.com")
public interface FeignRequestTossPayment {
    @PostMapping(value = "/v1/billing/{billingKey}", consumes = "application/json")
    ResponseEntity<AutoPaymentResponseDto> requestPayment(
            @PathVariable("billingKey") String billingKey,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AutoPaymentRequestDto requestDto
            );
}

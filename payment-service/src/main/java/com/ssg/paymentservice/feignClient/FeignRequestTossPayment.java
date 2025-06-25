package com.ssg.paymentservice.feignClient;

import com.ssg.paymentservice.dto.requestdto.AutoPaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "feignRequestTossPayment", url = "http://api.tosspayments/com")
public interface FeignRequestTossPayment {
    @PostMapping(value = "/v1/billing/{billingKey}", consumes = "application/json")
    ResponseEntity<String> requestPayment(
            @PathVariable("billingKey") String billingKey,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AutoPaymentRequestDto requestDto
            );
}

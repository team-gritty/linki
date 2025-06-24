package com.ssg.paymentservice.feignClient;

import com.ssg.paymentservice.dto.responsedto.PayInfluencerEmailNameResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "feignAutoPayment", url = "http://localhost:8000/v1/integration-service/api")
public interface FeignAutoPayment {
    @GetMapping
    ResponseEntity<PayInfluencerEmailNameResponseDto> getPayInfluencerEmailName(
            @RequestHeader("Authorization") String authorizationHeader);
}

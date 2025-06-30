package com.ssg.paymentservice.feignClient;

import com.ssg.paymentservice.dto.responsedto.PayInfluencerEmailNameResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "feignGetEmailNameByUserId", url = "http://v1/integration-service/api")
public interface FeignGetEmailNameByToken {
    //토큰값으로 이름 , 이메일 가져옴
    @GetMapping("/user/email-name")
    ResponseEntity<PayInfluencerEmailNameResponseDto> getEmailNameByToken(
            @RequestHeader("Authorization") String bearerToken
            );
}

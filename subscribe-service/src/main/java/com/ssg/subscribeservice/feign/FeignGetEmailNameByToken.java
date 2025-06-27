package com.ssg.subscribeservice.feign;


import com.ssg.subscribeservice.dto.responseDto.PayInfluencerEmailNameResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "feignGetEmailNameByUserId", url = "http://localhost:8000/v1/api")
public interface FeignGetEmailNameByToken {
    //토큰값으로 이름 , 이메일 가져옴
    @GetMapping("/user/email-name")
    ResponseEntity<PayInfluencerEmailNameResponseDto> getEmailNameByToken(
            @RequestHeader("Authorization") String bearerToken
            );
}

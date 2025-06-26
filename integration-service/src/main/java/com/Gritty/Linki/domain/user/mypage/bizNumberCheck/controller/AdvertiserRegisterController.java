package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO.AdvertiserRegisterRequest;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service.AdvertiserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v1/api/user/bizCheck")
@Slf4j
@RequiredArgsConstructor
public class AdvertiserRegisterController {
    private final AdvertiserRegisterService advertiserRegisterService;

    @PostMapping("/register")
    public ResponseEntity<String> registerBizInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AdvertiserRegisterRequest request) {
        log.info("rhkdrhw");
        advertiserRegisterService.saveBizInfo(
                userDetails,
                request.getBusinessNumber(),
                request.getCompanyName(),
                userDetails.getUserId()
        );

        return ResponseEntity.ok("등록 완료");
    }
}

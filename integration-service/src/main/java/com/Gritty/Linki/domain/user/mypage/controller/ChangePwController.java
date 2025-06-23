package com.Gritty.Linki.domain.user.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;
import com.Gritty.Linki.domain.user.mypage.service.UserMypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
@Slf4j
public class ChangePwController {

    private final UserMypageService userMypageService;

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody UserPasswordChangeRequestDto request) {
        try {
            userMypageService.changePassword(user.getUserId(), request);
            return ResponseEntity.ok().body(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}

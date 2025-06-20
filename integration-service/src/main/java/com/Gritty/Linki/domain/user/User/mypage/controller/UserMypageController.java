package com.Gritty.Linki.domain.user.User.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.User.mypage.service.UserMypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
public class UserMypageController {

    private final UserMypageService userMypageService;

    @GetMapping("/mypage")
    public ResponseEntity<UserMypageResponseDto> getUserMypage(@AuthenticationPrincipal CustomUserDetails user) {
        UserMypageResponseDto mypage = userMypageService.getUserMypage(user.getUserId());
        return ResponseEntity.ok(mypage);
    }

    @PatchMapping("/mypage")
    public ResponseEntity<?> updateUserMypage(@AuthenticationPrincipal CustomUserDetails user,
                                             @RequestBody UserMypageRequestDto request) {
        userMypageService.updateUserMypage(user.getUserId(), request);
        return ResponseEntity.ok().body(Map.of("message", "마이페이지 정보가 성공적으로 업데이트되었습니다."));
    }
} 
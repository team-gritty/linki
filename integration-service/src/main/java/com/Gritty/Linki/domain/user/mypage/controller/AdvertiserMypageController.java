package com.Gritty.Linki.domain.user.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;
import com.Gritty.Linki.domain.user.mypage.service.AdvertiserMypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/api/advertiser")
@RequiredArgsConstructor
@Slf4j
public class AdvertiserMypageController {

    private final AdvertiserMypageService advertiserMypageService;

    @GetMapping("/mypage")
    public ResponseEntity<?> getAdvertiserMypage(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            // 광고주 권한 확인
            String userRole = user.getAuthorities().iterator().next().getAuthority();
            log.info("사용자 권한 확인 - userId: {}, userRole: {}", user.getUserId(), userRole);
            if (!"ROLE_ADVERTISER".equals(userRole)) {
                log.error("광고주 권한이 없습니다. - userId: {}, userRole: {}", user.getUserId(), userRole);
                return ResponseEntity.status(403).body(Map.of("message", "광고주 권한이 필요합니다."));
            }
            
            log.info("광고주 마이페이지 조회 요청 - userId: {}", user.getUserId());
            UserMypageResponseDto mypage = advertiserMypageService.getAdvertiserMypage(user.getUserId());
            log.info("광고주 마이페이지 조회 성공 - userName: {}", mypage.getUserName());
            return ResponseEntity.ok(mypage);
        } catch (Exception e) {
            log.error("광고주 마이페이지 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "마이페이지 정보를 불러오는데 실패했습니다."));
        }
    }

    @PatchMapping("/mypage")
    public ResponseEntity<?> updateAdvertiserMypage(@AuthenticationPrincipal CustomUserDetails user,
                                                   @RequestBody UserMypageRequestDto request) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            // 광고주 권한 확인
            String userRole = user.getAuthorities().iterator().next().getAuthority();
            if (!"ROLE_ADVERTISER".equals(userRole)) {
                log.error("광고주 권한이 없습니다. - userId: {}, userRole: {}", user.getUserId(), userRole);
                return ResponseEntity.status(403).body(Map.of("message", "광고주 권한이 필요합니다."));
            }
            
            log.info("광고주 마이페이지 업데이트 요청 - userId: {}, userName: {}", user.getUserId(), request.getUserName());
            advertiserMypageService.updateAdvertiserMypage(user.getUserId(), request);
            log.info("광고주 마이페이지 업데이트 성공");
            return ResponseEntity.ok().body(Map.of("message", "마이페이지 정보가 성공적으로 업데이트되었습니다."));
        } catch (Exception e) {
            log.error("광고주 마이페이지 업데이트 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "마이페이지 정보 업데이트에 실패했습니다."));
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changeAdvertiserPassword(@AuthenticationPrincipal CustomUserDetails user,
                                                     @RequestBody UserPasswordChangeRequestDto request) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            // 광고주 권한 확인
            String userRole = user.getAuthorities().iterator().next().getAuthority();
            if (!"ROLE_ADVERTISER".equals(userRole)) {
                log.error("광고주 권한이 없습니다. - userId: {}, userRole: {}", user.getUserId(), userRole);
                return ResponseEntity.status(403).body(Map.of("message", "광고주 권한이 필요합니다."));
            }
            
            log.info("광고주 비밀번호 변경 요청 - userId: {}", user.getUserId());
            advertiserMypageService.changeAdvertiserPassword(user.getUserId(), request);
            log.info("광고주 비밀번호 변경 성공");
            return ResponseEntity.ok().body(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            log.error("광고주 비밀번호 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("광고주 비밀번호 변경 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
} 
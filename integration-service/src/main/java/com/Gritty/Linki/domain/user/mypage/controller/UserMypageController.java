package com.Gritty.Linki.domain.user.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.service.UserMypageService;
import com.Gritty.Linki.entity.User;
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
public class UserMypageController {

    private final UserMypageService userMypageService;

    /**
     * 현재 로그인한 사용자의 기본 정보 반환
     * 권한 변경 후 프론트엔드에서 최신 사용자 정보를 가져오기 위해 사용
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            // DB에서 최신 사용자 정보 조회 (권한 변경이 반영된 최신 정보)
            User currentUser = userMypageService.findUserById(user.getUserId());
            
            Map<String, Object> response = Map.of(
                "userId", currentUser.getUserId(),
                "userRole", currentUser.getUserRole(),
                "userName", currentUser.getUserName(),
                "userEmail", currentUser.getUserEmail()
            );
            
            log.info("현재 사용자 정보 조회 성공 - userId: {}, userRole: {}", 
                    currentUser.getUserId(), currentUser.getUserRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "사용자 정보를 불러오는데 실패했습니다."));
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getUserMypage(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            log.info("마이페이지 조회 요청 - userId: {}", user.getUserId());
            UserMypageResponseDto mypage = userMypageService.getUserMypage(user.getUserId());
            log.info("마이페이지 조회 성공 - userName: {}", mypage.getUserName());
            return ResponseEntity.ok(mypage);
        } catch (Exception e) {
            log.error("마이페이지 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "마이페이지 정보를 불러오는데 실패했습니다."));
        }
    }

    @PatchMapping("/mypage")
    public ResponseEntity<?> updateUserMypage(@AuthenticationPrincipal CustomUserDetails user,
                                             @RequestBody UserMypageRequestDto request) {
        try {
            if (user == null) {
                log.error("사용자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            log.info("마이페이지 업데이트 요청 - userId: {}, userName: {}", user.getUserId(), request.getUserName());
            userMypageService.updateUserMypage(user.getUserId(), request);
            log.info("마이페이지 업데이트 성공");
            return ResponseEntity.ok().body(Map.of("message", "마이페이지 정보가 성공적으로 업데이트되었습니다."));
        } catch (Exception e) {
            log.error("마이페이지 업데이트 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "마이페이지 정보 업데이트에 실패했습니다."));
        }
    }
} 
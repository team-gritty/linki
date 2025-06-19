package com.Gritty.Linki.domain.user.User.controller;

import com.Gritty.Linki.domain.user.User.dto.FindIdRequestDto;
import com.Gritty.Linki.domain.user.User.dto.FindIdResponseDto;
import com.Gritty.Linki.domain.user.User.dto.VerificationRequestDto;
import com.Gritty.Linki.domain.user.User.dto.VerificationResponseDto;
import com.Gritty.Linki.domain.user.User.service.FindIdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/find-id")
public class FindIdController {

    private final FindIdService findIdService;

    /**
     * 인증번호 발송
     */
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody FindIdRequestDto requestDto) {
        try {
            log.info("인증번호 발송 요청: {}", requestDto.getUserEmail());
            
            boolean success = findIdService.sendVerificationCode(requestDto.getUserName(), requestDto.getUserEmail());
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "인증번호가 이메일로 발송되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "일치하는 회원 정보를 찾을 수 없습니다."
                ));
            }
        } catch (Exception e) {
            log.error("인증번호 발송 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인증번호 발송 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 인증번호 확인 및 아이디 반환
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody VerificationRequestDto requestDto) {
        try {
            log.info("인증번호 확인 요청: {}", requestDto.getUserEmail());
            
            FindIdResponseDto responseDto = findIdService.verifyCode(
                requestDto.getUserName(), 
                requestDto.getUserEmail(), 
                requestDto.getVerificationCode()
            );
            
            if (responseDto != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", responseDto.getUserId(),
                    "message", "인증이 완료되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "인증번호가 올바르지 않습니다."
                ));
            }
        } catch (Exception e) {
            log.error("인증번호 확인 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인증번호 확인 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 인증번호 재발송
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody FindIdRequestDto requestDto) {
        try {
            log.info("인증번호 재발송 요청: {}", requestDto.getUserEmail());
            
            boolean success = findIdService.resendVerificationCode(requestDto.getUserName(), requestDto.getUserEmail());
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "인증번호가 재발송되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "인증번호 재발송에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("인증번호 재발송 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인증번호 재발송 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 디버깅용: 사용자 정보 확인
     */
    @GetMapping("/check-user")
    public ResponseEntity<Map<String, Object>> checkUser(@RequestParam String userName, @RequestParam String userEmail) {
        try {
            log.info("사용자 정보 확인 요청: userName='{}', userEmail='{}'", userName, userEmail);
            
            String userId = findIdService.checkUser(userName, userEmail);
            
            if (userId != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", userId,
                    "message", "사용자를 찾았습니다."
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "일치하는 사용자 정보를 찾을 수 없습니다."
                ));
            }
        } catch (Exception e) {
            log.error("사용자 정보 확인 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "사용자 정보 확인 중 오류가 발생했습니다."
            ));
        }
    }
} 
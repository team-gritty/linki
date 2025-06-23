package com.Gritty.Linki.domain.account.account.controller;

import com.Gritty.Linki.domain.account.dto.FindPasswordRequestDto;
import com.Gritty.Linki.domain.account.dto.FindPasswordVerificationRequestDto;
import com.Gritty.Linki.domain.account.dto.FindPasswordChangeRequestDto;
import com.Gritty.Linki.domain.account.account.service.FindPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/find-password")
public class FindPasswordController {

    private final FindPasswordService findPasswordService;

    /**
     * 인증번호 발송
     */
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody FindPasswordRequestDto requestDto) {
        try {
            log.info("비밀번호 찾기 인증번호 발송 요청: userName='{}', userEmail='{}'", requestDto.getUserName(), requestDto.getUserEmail());
            
            boolean success = findPasswordService.sendVerificationCode(
                requestDto.getUserName(), 
                requestDto.getUserLoginId(), 
                requestDto.getUserEmail()
            );
            
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
            log.error("비밀번호 찾기 인증번호 발송 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인증번호 발송 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 인증번호 확인
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody FindPasswordVerificationRequestDto requestDto) {
        try {
            log.info("비밀번호 찾기 인증번호 확인 요청: userName='{}', userEmail='{}'", requestDto.getUserName(), requestDto.getUserEmail());
            
            boolean success = findPasswordService.verifyCode(
                requestDto.getUserName(),
                requestDto.getUserLoginId(), 
                requestDto.getUserEmail(), 
                requestDto.getVerificationCode()
            );
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "인증이 완료되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "인증번호가 올바르지 않습니다."
                ));
            }
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증번호 확인 중 오류 발생", e);
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
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody FindPasswordRequestDto requestDto) {
        try {
            log.info("비밀번호 찾기 인증번호 재발송 요청: userName='{}', userEmail='{}'", requestDto.getUserName(), requestDto.getUserEmail());
            
            boolean success = findPasswordService.resendVerificationCode(
                requestDto.getUserName(),
                requestDto.getUserLoginId(), 
                requestDto.getUserEmail()
            );
            
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
            log.error("비밀번호 찾기 인증번호 재발송 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인증번호 재발송 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody FindPasswordChangeRequestDto requestDto) {
        try {
            log.info("비밀번호 변경 요청: userName='{}', userEmail='{}'", requestDto.getUserName(), requestDto.getUserEmail());
            
            boolean success = findPasswordService.changePassword(
                requestDto.getUserName(),
                requestDto.getUserLoginId(),
                requestDto.getUserEmail(),
                requestDto.getVerificationCode(),
                requestDto.getNewPassword()
            );
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "비밀번호가 성공적으로 변경되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "비밀번호 변경에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "비밀번호 변경 중 오류가 발생했습니다."
            ));
        }
    }

    /**
     * 디버깅용: 사용자 정보 확인
     */
    @GetMapping("/check-user")
    public ResponseEntity<Map<String, Object>> checkUser(@RequestParam String userName, @RequestParam String userLoginId, @RequestParam String userEmail) {
        try {
            log.info("사용자 정보 확인 요청: userName='{}', userLoginId='{}', userEmail='{}'", userName, userLoginId, userEmail);
            
            boolean exists = findPasswordService.checkUser(userName, userLoginId, userEmail);
            
            if (exists) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
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
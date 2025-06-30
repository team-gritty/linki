
package com.linki.admin_integration_service.domain.account.controller;

import com.linki.admin_integration_service.config.CustomUserDetails;
import com.linki.admin_integration_service.domain.account.dto.AdminMypageRequestDto;
import com.linki.admin_integration_service.domain.account.dto.AdminMypageResponseDto;
import com.linki.admin_integration_service.domain.account.dto.AdminPasswordChangeRequestDto;
import com.linki.admin_integration_service.domain.account.dto.JoinDTO;
import com.linki.admin_integration_service.domain.account.dto.RequestJoinDTO;
import com.linki.admin_integration_service.domain.account.service.AccountService;
import com.linki.admin_integration_service.entity.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin")
@Slf4j

public class AccountController {
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody RequestJoinDTO requestJoinDTO){
        log.info("어드민 회원가입 백엔드 시작");
        System.out.println("=== 회원가입 요청 시작 ===");
        System.out.println("requestJoinDTO: " + requestJoinDTO);

        JoinDTO joinDTO = new ModelMapper().map(requestJoinDTO, JoinDTO.class);
        System.out.println("joinDTO: " + joinDTO);

        if(!StringUtils.hasLength(joinDTO.getAdminLoginId()) || !StringUtils.hasLength(joinDTO.getAdminLoginPw()) || !StringUtils.hasLength(joinDTO.getAdminName())
        || !StringUtils.hasLength(joinDTO.getAdminPhone()) || !StringUtils.hasLength(joinDTO.getAdminEmail())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String loginId = requestJoinDTO.getAdminLoginId();
        System.out.println("중복 체크 시작: " + loginId);
        
        Admin existingAdmin = accountService.find(loginId);
        System.out.println("중복 체크 결과: " + existingAdmin);
        
        if(StringUtils.hasLength(loginId) && existingAdmin != null){
            System.out.println("아이디 중복 발견");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","아이디 중복"));
        }

        try {
            System.out.println("회원가입 저장 시작");
            accountService.save(joinDTO);
            System.out.println("회원가입 저장 완료");
            return ResponseEntity.ok(Map.of("message","회원가입이 완료되었습니다."));
        } catch (Exception e) {
            System.err.println("회원가입 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원가입 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getAdminMypage(@AuthenticationPrincipal CustomUserDetails admin) {
        try {
            if (admin == null) {
                log.error("관리자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            String adminId = admin.getUserId(); // JWT에서 가져온 userId는 실제로는 adminId
            log.info("관리자 마이페이지 조회 요청 - adminId: {}", adminId);
            AdminMypageResponseDto mypage = accountService.getAdminMypage(adminId);
            log.info("관리자 마이페이지 조회 성공 - adminName: {}", mypage.getAdminName());
            return ResponseEntity.ok(mypage);
        } catch (Exception e) {
            log.error("관리자 마이페이지 조회 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "관리자 정보를 불러오는데 실패했습니다."));
        }
    }

    @PatchMapping("/mypage")
    public ResponseEntity<?> updateAdminMypage(@AuthenticationPrincipal CustomUserDetails admin,
                                              @RequestBody AdminMypageRequestDto request) {
        try {
            if (admin == null) {
                log.error("관리자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            String adminId = admin.getUserId(); // JWT에서 가져온 userId는 실제로는 adminId
            log.info("관리자 마이페이지 업데이트 요청 - adminId: {}, adminName: {}", 
                    adminId, request.getAdminName());
            accountService.updateAdminMypage(adminId, request);
            log.info("관리자 마이페이지 업데이트 성공");
            return ResponseEntity.ok().body(Map.of("message", "관리자 정보가 성공적으로 업데이트되었습니다."));
        } catch (Exception e) {
            log.error("관리자 마이페이지 업데이트 실패", e);
            return ResponseEntity.status(500).body(Map.of("message", "관리자 정보 업데이트에 실패했습니다."));
        }
    }

    @PatchMapping("/mypage/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails admin,
                                           @RequestBody AdminPasswordChangeRequestDto request) {
        try {
            if (admin == null) {
                log.error("관리자 인증 정보가 없습니다.");
                return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
            }
            
            String adminId = admin.getUserId(); // JWT에서 가져온 userId는 실제로는 adminId
            log.info("관리자 비밀번호 변경 요청 - adminId: {}", adminId);
            accountService.changePassword(adminId, request);
            log.info("관리자 비밀번호 변경 성공");
            return ResponseEntity.ok().body(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            log.error("관리자 비밀번호 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("관리자 비밀번호 변경 중 예상치 못한 오류", e);
            return ResponseEntity.status(500).body(Map.of("message", "비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
}


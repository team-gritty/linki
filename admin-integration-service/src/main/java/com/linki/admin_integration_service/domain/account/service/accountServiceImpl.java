package com.linki.admin_integration_service.domain.account.service;


import com.linki.admin_integration_service.domain.account.dto.AdminMypageRequestDto;
import com.linki.admin_integration_service.domain.account.dto.AdminMypageResponseDto;
import com.linki.admin_integration_service.domain.account.dto.AdminPasswordChangeRequestDto;

import com.linki.admin_integration_service.domain.account.dto.JoinDTO;
import com.linki.admin_integration_service.domain.account.repository.AccountRepository;
import com.linki.admin_integration_service.entity.Admin;
import com.linki.admin_integration_service.util.IdGenerator;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j

public class accountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(JoinDTO joinDTO) {
        Admin admin = Admin.builder()
                .adminId(IdGenerator.adminId())
                .adminLoginId(joinDTO.getAdminLoginId())
                .adminLoginPw(passwordEncoder.encode(joinDTO.getAdminLoginPw()))
                .adminName(joinDTO.getAdminName())
                .adminPhone(joinDTO.getAdminPhone())
                .adminEmail(joinDTO.getAdminEmail())
                .adminAddress(joinDTO.getAdminAddress())

                .adminEnterDay(LocalDate.now())  // 현재 날짜로 설정
                .adminStatus("PENDING")  // 기본 상태는 승인 대기

                .build();

        accountRepository.save(admin);

    }


    @Override
    public Admin find(String adminLoginId) {
        return accountRepository.findByAdminLoginId(adminLoginId)
                .orElse(null);  // 찾을 수 없으면 null 반환
    }

    @Override
    @Transactional(readOnly = true)
    public AdminMypageResponseDto getAdminMypage(String adminId) {
        log.info("관리자 마이페이지 조회 시작 - adminId: {}", adminId);
        
        // adminId로 adminLoginId 조회
        String adminLoginId = accountRepository.findAdminLoginIdByAdminId(adminId)
                .orElseThrow(() -> {
                    log.error("관리자를 찾을 수 없습니다. - adminId: {}", adminId);
                    return new RuntimeException("관리자를 찾을 수 없습니다.");
                });
        
        // adminLoginId로 관리자 정보 조회
        Admin admin = accountRepository.findByAdminLoginId(adminLoginId)
                .orElseThrow(() -> {
                    log.error("관리자를 찾을 수 없습니다. - adminLoginId: {}", adminLoginId);
                    return new RuntimeException("관리자를 찾을 수 없습니다.");
                });

        log.info("관리자 조회 성공 - adminName: {}, adminEmail: {}", admin.getAdminName(), admin.getAdminEmail());

        AdminMypageResponseDto response = new AdminMypageResponseDto();
        response.setAdminLoginId(admin.getAdminLoginId());
        response.setAdminName(admin.getAdminName());
        response.setAdminPhone(admin.getAdminPhone());
        response.setAdminEmail(admin.getAdminEmail());
        response.setAdminAddress(admin.getAdminAddress());
        response.setAdminEnterDay(admin.getAdminEnterDay());

        log.info("마이페이지 응답 생성 완료 - adminName: {}", response.getAdminName());
        return response;
    }

    @Override
    public void updateAdminMypage(String adminId, AdminMypageRequestDto request) {
        log.info("관리자 마이페이지 업데이트 시작 - adminId: {}", adminId);
        
        // adminId로 adminLoginId 조회
        String adminLoginId = accountRepository.findAdminLoginIdByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
        
        // adminLoginId로 관리자 정보 조회
        Admin admin = accountRepository.findByAdminLoginId(adminLoginId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));

        admin.setAdminName(request.getAdminName());
        admin.setAdminPhone(request.getAdminPhone());
        admin.setAdminEmail(request.getAdminEmail());
        admin.setAdminAddress(request.getAdminAddress());

        accountRepository.save(admin);
        log.info("관리자 마이페이지 업데이트 완료 - adminName: {}", request.getAdminName());
    }

    @Override
    public void changePassword(String adminId, AdminPasswordChangeRequestDto request) {
        log.info("관리자 비밀번호 변경 시작 - adminId: {}", adminId);
        
        // adminId로 adminLoginId 조회
        String adminLoginId = accountRepository.findAdminLoginIdByAdminId(adminId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
        
        // adminLoginId로 관리자 정보 조회
        Admin admin = accountRepository.findByAdminLoginId(adminLoginId)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getAdminLoginPw())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), admin.getAdminLoginPw())) {
            throw new RuntimeException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 새 비밀번호로 변경
        admin.setAdminLoginPw(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(admin);
        
        log.info("관리자 비밀번호 변경 완료 - adminId: {}", adminId);
    }

}

package com.linki.admin_integration_service.domain.account.service;



import com.linki.admin_integration_service.domain.account.dto.AdminMypageRequestDto;
import com.linki.admin_integration_service.domain.account.dto.AdminMypageResponseDto;
import com.linki.admin_integration_service.domain.account.dto.AdminPasswordChangeRequestDto;
import com.linki.admin_integration_service.domain.account.dto.JoinDTO;
import com.linki.admin_integration_service.entity.Admin;


public interface AccountService {

    void save(JoinDTO joinDTO);


    Admin find(String adminLoginId);
    
    // 마이페이지 관련 메서드 - adminId 매개변수 사용
    AdminMypageResponseDto getAdminMypage(String adminId);
    void updateAdminMypage(String adminId, AdminMypageRequestDto request);
    void changePassword(String adminId, AdminPasswordChangeRequestDto request);

}

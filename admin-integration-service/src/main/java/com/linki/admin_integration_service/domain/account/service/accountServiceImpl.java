package com.linki.admin_integration_service.domain.account.service;

import com.linki.admin_integration_service.domain.account.dto.JoinDTO;
import com.linki.admin_integration_service.domain.account.repository.AccountRepository;
import com.linki.admin_integration_service.entity.Admin;
import com.linki.admin_integration_service.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
                .build();

        accountRepository.save(admin);

    }
}

package com.linki.admin_integration_service.config;


import com.linki.admin_integration_service.domain.account.repository.AccountRepository;
import com.linki.admin_integration_service.entity.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.AbstractDocument;

@Service
@RequiredArgsConstructor
//매니저에서 가져다 쓸 객체 (회원객체)
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String adminname) throws UsernameNotFoundException {
        Admin admin = accountRepository.findByAdminLoginId(adminname).orElseThrow(()->
                new UsernameNotFoundException(adminname+"는 없습니다."));

        return new CustomUserDetails(admin.getAdminId(),admin.getAdminLoginId(),admin.getAdminLoginPw(), admin.getAdminStatus());
    }
}

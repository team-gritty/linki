package com.ssg.paymentservice.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtilImpl implements SecurityUtil {

    public String getCurrentUserId(){
        Optional<Authentication> authentication = Optional
                .ofNullable(SecurityContextHolder
                        .getContext()
                        .getAuthentication());
        return authentication.orElseThrow(() ->
                new RuntimeException("유저아이디 시큐리티홀더이 존재하지 않음"))
                .getPrincipal()
                .toString();
    }

    public String getCurrentUserRole(){
        Optional<Authentication> authentication = Optional
                .ofNullable(SecurityContextHolder
                        .getContext()
                        .getAuthentication());
        return authentication.orElseThrow(() ->
                new RuntimeException("인증정보 존재하지 않음(권한 확인 수행중)"))
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() ->
                        new RuntimeException("권한정보 존재하지 않음"));
    }
}

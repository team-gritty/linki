package com.linki.admin_integration_service.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Builder


//유저 디테일즈 서비스에서 사용할 객체
public class CustomUserDetails implements UserDetails {
    private final String userId;
    private final String userLoginId;
    private final String password;
    private final String status;


    @Builder
    public CustomUserDetails(String userId, String userLoginId, String password, String status) {
        this.userId = userId;
        this.userLoginId = userLoginId;
        this.password = password;
        this.status = status;

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userLoginId; // 또는 userId도 가능
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return new ArrayList<>();
    }

    // 나머지 true 리턴
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public String getUserId() {
        return userId;
    }
}
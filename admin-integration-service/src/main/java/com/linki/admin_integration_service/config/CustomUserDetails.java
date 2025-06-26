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
    private final String role;

    @Builder
    public CustomUserDetails(String userId, String userLoginId, String password, String role) {
        this.userId = userId;
        this.userLoginId = userLoginId;
        this.password = password;
        this.role = role;
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
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        return authorities;
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
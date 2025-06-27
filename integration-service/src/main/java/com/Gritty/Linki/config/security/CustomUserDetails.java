package com.Gritty.Linki.config.security;

import com.Gritty.Linki.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.userLoginId = user.getUserLoginId();
        this.password = user.getUserLoginPw();
        this.role = user.getUserRole();
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
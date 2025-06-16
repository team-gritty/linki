package com.Gritty.Linki.config.security;

public class JwtUtil {
    public boolean validateToken(String token) {
        // TODO: 실제 검증 로직 (만료, 서명 등)
        return true;
    }

    public String extractRole(String token) {
        // TODO: 실제 파싱 로직으로 교체
        return "USER";
    }
}

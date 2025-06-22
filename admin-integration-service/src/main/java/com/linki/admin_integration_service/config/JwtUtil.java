package com.linki.admin_integration_service.config;

/**
 * JwtUtil 클래스는 JWT 토큰에서 필요한 정보를 추출하는 유틸리티 클래스입니다.
 */
public class JwtUtil {

    /**
     * JWT 토큰에서 사용자 권한(role)을 추출합니다.
     * 현재는 하드코딩으로 "ADMIN"을 반환하고 있으며,
     * 추후 실제 JWT 파싱 로직으로 교체되어야 합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 사용자 역할 (예: "ADMIN", "USER" 등)
     */
    public static String extractRole(String token) {
        // TODO: 아래 부분은 JWT에서 role 추출하는 실제 로직으로 교체 필요
        // 현재는 ADMIN만 하드코딩으로 체크함
        return "ADMIN";
    }
}

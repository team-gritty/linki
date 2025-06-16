package com.Gritty.Linki.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// 임시 구현, 추후 변경 가능

public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 요청 전처리 단계에서 JWT 토큰을 검사합니다.
     * 1. Authorization 헤더의 존재 및 Bearer 형식 확인
     * 2. JWT 파싱 및 유효성 검증
     * 3. 사용자 역할(role) 추출 및 request에 저장
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return false;
        }

        token = token.substring(7); // Bearer 제거

        try {
            if (!jwtUtil.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired or invalid token");
                return false;
            }

            // 사용자 role 추출 (예: INFLUENCER, ADVERTISER, USER 등)
            String role = jwtUtil.extractRole(token);
            request.setAttribute("userRole", role); // 컨트롤러 단에서 꺼내쓸 수 있음

            return true;

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed");
            return false;
        }
    }
}

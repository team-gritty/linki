package com.linki.admin_integration_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JwtInterceptor는 HTTP 요청의 Authorization 헤더에 포함된 JWT 토큰을 검사하는 인터셉터입니다.
 * 유효한 토큰이 존재하고, 사용자 권한(role)이 "ADMIN"일 경우에만 요청을 허용합니다.
 */
public class JwtInterceptor implements HandlerInterceptor {

    /**
     * 요청 전처리 단계에서 JWT 토큰을 검사합니다.
     * 1. Authorization 헤더 유무 및 Bearer 형식 확인
     * 2. JWT 토큰 파싱하여 role 정보 추출
     * 3. role이 ADMIN이 아닐 경우 403 반환
     * 4. 예외 발생 시 401 반환
     *
     * @return 요청이 허용되면 true, 그렇지 않으면 false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        // Authorization: Bearer abc.def.ghi 형식 검사
        if (token == null || !token.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return false;
        }

        token = token.substring(7); // Bearer 제거

        try {
            // TODO: 아래 부분은 JWT에서 role 추출하는 실제 로직으로 교체 필요
            // 현재는 ADMIN만 하드코딩으로 체크함
            // JWT 파싱해서 role 뽑는 로직
            String role = JwtUtil.extractRole(token);

            // 예: ADMIN 권한만 접근 허용
            if (!"ADMIN".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: ADMIN role required");
                return false;
            }

            return true; // 통과
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return false;
        }
    }
}
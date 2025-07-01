package com.linki.admin_integration_service.config;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
//jwt 검증 필터
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        log.info("요청 URL: {}, Method: {}", request.getRequestURI(), request.getMethod());
        log.info("Authorization 헤더: {}", authorization);

        // 로그인 경로는 토큰 검증을 건너뛰기
        if (request.getRequestURI().equals("/v1/api/nonuser/login")) {
            log.info("로그인 경로이므로 토큰 검증을 건너뜁니다.");
            filterChain.doFilter(request, response);
            return;
        }

        //헤더 토큰 확인
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("인증헤더 없어");
            filterChain.doFilter(request, response);
            return;
        }

        //베리어 제거
        String token = authorization.split(" ")[1];

        if(jwtUtil.isTokenExpired(token)){
            log.info("토큰 만료");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"토큰이 만료되었습니다.\"}");
            return;
        }

        try {
            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);
            log.info("JWT 토큰에서 추출한 정보 - userId: {}, role: {}", userId, role);

            //커스텀 디테일즈
            //레포 호출하면 성능 이슈
            CustomUserDetails customUserDetails = new CustomUserDetails(userId,"-","-", role);
            //시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            //쓰레드로컬 시큐리티컨텍스트홀더에 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("등록된 인증 객체: {}", SecurityContextHolder.getContext().getAuthentication());
            log.info("권한 목록: {}", customUserDetails.getAuthorities());
            //이제 넘겨~
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.info("토큰 만료 (ExpiredJwtException)");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"토큰이 만료되었습니다.\"}");
            return;
        }
    }
}

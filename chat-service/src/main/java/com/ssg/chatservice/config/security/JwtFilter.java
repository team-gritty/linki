package com.ssg.chatservice.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//jwt 검증 필터
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        //헤더 토큰 확인
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            // SSE 요청인 경우 쿼리 파라미터에서 토큰 확인
            if(request.getRequestURI().contains("/sse/")) {
                String queryToken = request.getParameter("token");
                if(queryToken != null) {
                    // 쿼리 파라미터 토큰으로 인증 처리
                    processToken(queryToken, request, response, filterChain);
                    return;
                }
            }
            log.info("인증헤더 없어");
            filterChain.doFilter(request, response);
            return;
        }

        //베리어 제거
        String token = authorization.split(" ")[1];
        processToken(token, request, response, filterChain);
    }
    
    private void processToken(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (jwtUtil.isTokenExpired(token)) {
            log.info("토큰 만료");
            filterChain.doFilter(request, response);
            return;
        }


        String userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        log.debug("role in token = {}", role);

        // Authentication 객체 생성 및 등록
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

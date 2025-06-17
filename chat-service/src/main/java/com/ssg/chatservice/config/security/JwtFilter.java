package com.ssg.chatservice.config.security;

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
            filterChain.doFilter(request, response);
            return;
        }


        String userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        log.debug("role in token = {}", role);




        //커스텀 디테일즈
        //레포 호출하면 성능 이슈
        CustomUserDetails customUserDetails = new CustomUserDetails(userId,"-","-",role);
        //시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //쓰레드로컬 시큐리티컨텍스트홀더에 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        //이제 넘겨~
        filterChain.doFilter(request, response);


    }
}

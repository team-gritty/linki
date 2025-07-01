package com.ssg.chatservice.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf
        http.csrf(AbstractHttpConfigurer::disable);
        //form 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                //권한별 설정
                .requestMatchers("/v1/chat-service/ws/chat/**").permitAll()
                .requestMatchers("/v1/chat-service/api/nonuser/**").permitAll()
                .requestMatchers("/v1/chat-service/api/sse/**").permitAll()
                .requestMatchers("/v1/chat-service/api/user/**").hasAnyRole("USER","INFLUENCER","ADVERTISER")
                .requestMatchers("/v1/chat-service/api/influencer/**").hasRole("INFLUENCER")
                .requestMatchers("/v1/chat-service/api/advertiser/**").hasRole("ADVERTISER")
                .requestMatchers("/v1/chat-service/api/authuser/**").hasAnyRole("USER","ADVERTISER", "INFLUENCER")
                .anyRequest().authenticated());
        //파싱후 권한 검증
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        //jwt기떄문에 세션 무상태
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        
        return new BCryptPasswordEncoder();
    }
}

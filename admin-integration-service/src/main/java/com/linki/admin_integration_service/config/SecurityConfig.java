//package com.linki.admin_integration_service.config;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final AuthenticationConfiguration authenticationConfiguration;
//    private final JwtUtil jwtUtil;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        //csrf
//        http.csrf(AbstractHttpConfigurer::disable);
//        //form 로그인 방식 disable
//        http.formLogin(AbstractHttpConfigurer::disable);
//        http.httpBasic(AbstractHttpConfigurer::disable);
//        http.authorizeHttpRequests(auth -> auth
//                //권한별 설정
//                .requestMatchers("/api/**").permitAll()
//                .requestMatchers("/v1/api/admin/**").permitAll()
//
//                .anyRequest().permitAll());
//
//        // ✅ OAuth2 로그인 추가
//        http.oauth2Login(oauth2 -> oauth2
//                .userInfoEndpoint(userInfo -> userInfo
//                        .userService(oAuthService))
//                .successHandler(oAuth2LoginSuccessHandler)
//        );
//
//        // 로그인 필터를 먼저 추가 (JwtFilter보다 먼저 실행)
//        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, userRepository, refreshTokenService), UsernamePasswordAuthenticationFilter.class);
//
//        // JWT 필터를 나중에 추가 (로그인 필터 이후에 실행)
//        http.addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);
//
//        //jwt기떄문에 세션 무상태
//        http.sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//
//
//        return new BCryptPasswordEncoder();
//    }
//}

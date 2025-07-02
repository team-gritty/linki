
package com.linki.admin_integration_service.config;


import com.linki.admin_integration_service.config.token.RefreshTokenService;
import com.linki.admin_integration_service.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final RefreshTokenService refreshTokenService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf
        http.csrf(AbstractHttpConfigurer::disable);
        //cors
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        //form 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                //권한별 설정
                .requestMatchers("v1/api/**").permitAll()
                .requestMatchers("v1/admin/api/redirect/{shortUrl}").permitAll()
                .requestMatchers("/v1/admin/api/**").hasRole("AGREEMENT")

                .anyRequest().permitAll());

        // ✅ OAuth2 로그인 추가

//        http.oauth2Login(oauth2 -> oauth2
//                .userInfoEndpoint(userInfo -> userInfo
//                        .userService(oAuthService))
//                .successHandler(oAuth2LoginSuccessHandler)
//        );

        // 로그인 필터를 먼저 추가 (JwtFilter보다 먼저 실행)
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, accountRepository, refreshTokenService), UsernamePasswordAuthenticationFilter.class);

        // JWT 필터를 나중에 추가 (로그인 필터 이후에 실행)
        http.addFilterAfter(new JwtFilter(jwtUtil), LoginFilter.class);

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3001"); // linki-admin 프론트엔드 포트
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

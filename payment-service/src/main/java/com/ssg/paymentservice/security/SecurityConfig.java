package com.ssg.paymentservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.jwt.secret}")
    private String secret;

    // JWT → Authentication 변환
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        return jwt -> {
            // 1) 권한 셋업 (claim “userRole”:  "ROLE_INFLUENCER" 등)
            Collection<GrantedAuthority> auth =
                    AuthorityUtils.commaSeparatedStringToAuthorityList(
                            jwt.getClaimAsString("userRole"));

            // 2) Principal 에 userId 저장
            String userId = jwt.getClaimAsString("userId");
            return new UsernamePasswordAuthenticationToken(userId, jwt, auth);
        };
    }

    //HS256 Decoder
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // 필터 체인
    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)          // or  .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthConverter())
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/payment-service/api/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
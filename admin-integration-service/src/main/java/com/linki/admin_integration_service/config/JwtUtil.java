package com.linki.admin_integration_service.config;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;



@Component
public class JwtUtil {
    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //유저 아이디 겟
    public String getUserId(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
        } catch (ExpiredJwtException e) {
            throw e; // ExpiredJwtException을 다시 던져서 JwtFilter에서 처리하도록 함
        }
    }

    //권한 겟
    public String getRole(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userRole", String.class);
        } catch (ExpiredJwtException e) {
            throw e; // ExpiredJwtException을 다시 던져서 JwtFilter에서 처리하도록 함
        }
    }

    //토큰 만료됬는지
    public Boolean isTokenExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 만료된 토큰이면 true 반환
        }
    }

    public String createJwtToken(String userId,  Long expiredMs) {
        return Jwts.builder()
                .claim("userId",userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiredMs * 1000))
                .signWith(secretKey)
                .compact();
    }

    public Map<String, Object> getBody(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e; // ExpiredJwtException을 다시 던져서 호출자에서 처리하도록 함
        }
    }


}

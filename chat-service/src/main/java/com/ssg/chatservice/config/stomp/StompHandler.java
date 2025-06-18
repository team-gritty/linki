package com.ssg.chatservice.config.stomp;

import com.ssg.chatservice.config.security.JwtUtil;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.exception.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;



/*웹소켓 연결 시 시큐리티 필터를 거치지 않으므로
인터셉터를 구현하는 StompHandler를 통해  jwt 파싱 후 인증 객체 세션 주입*/
@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtUtil jwtUtil;

    //클라이언트 소켓 연결 시도 시 실행 되는 메서드 오버라이드
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 메시지에서 STOMP 헤더 정보 추출
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        // 만약 이 메시지가 CONNECT 요청이면 (처음 연결 시도할 때만 실행)
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            // 클라이언트가 보내온 Authorization 헤더 값 꺼냄
            String token = accessor.getFirstNativeHeader("Authorization");
            // 헤더가 없거나 Bearer로 시작하지 않으면 예외 발생
            if(token ==null || !token.startsWith("Bearer ")){
                log.info("토큰 인증 불가");
                throw new ChatException(ErrorCode.JWT_TOKEN_FAIL);
            }
            //"Bearer "제거
            token = token.substring(7);
            //만료여부 확인
            if(jwtUtil.isTokenExpired(token)){
                log.info("토큰 만료");
                throw new ChatException(ErrorCode.JWT_TOKEN_END);
            }

            // 토큰에서 유저 ID 및 롤 추출
            String userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            // 스프링 시큐리티의 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, // Principal로 userId만 넣음
                    null,   // Password null
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)) // 권한 설정
            );

            // WebSocket 세션에 인증 객체를 저장 (이후 @MessageMapping에서 접근 가능)
            accessor.setUser(authentication);
        }

        // 메시지 체인 계속 진행
        return message;
    }
}

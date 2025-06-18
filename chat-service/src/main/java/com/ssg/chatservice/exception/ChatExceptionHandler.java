package com.ssg.chatservice.exception;

import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// com.ssg.chatservice.domain.chat.exception.ChatExceptionHandler
@RestControllerAdvice(basePackages = "com.ssg.chatservice.domain.chat")  // 이 패키지 내의 컨트롤러에만 적용됨
@Log4j2
public class ChatExceptionHandler {

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<?> handleChatException(ChatException e) {
        ErrorCode error = e.getErrorCode();
        log.error("채팅 도메인 예외 발생: {}", error.getMessage());
        return ResponseEntity.status(error.getStatus())
                .body(Map.of(
                        "code", error.name(),
                        "message", error.getMessage()
                ));
    }
}

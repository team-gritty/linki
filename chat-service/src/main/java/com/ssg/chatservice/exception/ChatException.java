package com.ssg.chatservice.exception;

import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private final ErrorCode errorCode;

    public ChatException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 생성자 호출
        this.errorCode = errorCode;
    }
}

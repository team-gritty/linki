package com.ssg.chatservice.domain.chat.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CHATROOM_ALREADY_EXIST(HttpStatus.CONFLICT,"이미 존재하는 채팅방 입니다.");

    private final HttpStatus status;
    private final String message;

}

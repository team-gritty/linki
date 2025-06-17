package com.ssg.chatservice.domain.chat.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CHATROOM_ALREADY_EXIST(HttpStatus.CONFLICT,"이미 존재하는 채팅방 입니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    PARTNER_API_FAILED(HttpStatus.BAD_GATEWAY, "파트너 API 호출에 실패했습니다.");




    private final HttpStatus status;
    private final String message;

}

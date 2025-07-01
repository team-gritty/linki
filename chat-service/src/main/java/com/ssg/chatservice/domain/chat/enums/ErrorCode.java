package com.ssg.chatservice.domain.chat.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CHATROOM_ALREADY_EXIST(HttpStatus.CONFLICT,"이미 존재하는 채팅방 입니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메세지가 존재하지 않습니다."),
    PARTNER_API_FAILED(HttpStatus.BAD_GATEWAY, "파트너 API 호출에 실패했습니다."),
    JWT_TOKEN_END(HttpStatus.UNAUTHORIZED,"토큰 만료"),
    JWT_TOKEN_FAIL(HttpStatus.UNAUTHORIZED,"토큰 인증 불가"),
    KAFKA_INVALID_EVENT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 이벤트 타입입니다."),
    KAFKA_DESERIALIZATION_ERROR(HttpStatus.BAD_REQUEST, "Kafka 메시지 역직렬화 실패"),
    KAFKA_NEGO_STATUS_NULL(HttpStatus.BAD_REQUEST, "이벤트에 연결된 계약 상태(NegoStatus)가 없습니다."),
    KAFKA_UNHANDLED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Kafka 이벤트 처리 중 예외 발생"),
    SSE_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SSE 연결에 실패했습니다."),
    SSE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SSE 데이터 전송에 실패했습니다."),
    SSE_EMITTER_NOT_FOUND(HttpStatus.NOT_FOUND, "SSE 연결 정보가 존재하지 않습니다."),
    SSE_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 SSE 오류가 발생했습니다.");





    private final HttpStatus status;
    private final String message;

}

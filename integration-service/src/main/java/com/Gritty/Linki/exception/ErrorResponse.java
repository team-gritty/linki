package com.Gritty.Linki.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 에러 응답의 일관된 포맷을 제공하는 DTO 클래스
 * 클라이언트에게 에러 정보를 전달할 때 사용
 * 
 * 사용 예시:
 * ErrorResponse response = ErrorResponse.of(
 * HttpStatus.BAD_REQUEST,
 * ErrorCode.INVALID_INPUT_VALUE,
 * "/api/users"
 * );
 */
@Getter // Lombok: 모든 필드의 getter 메서드를 자동 생성
@Builder // Lombok: 빌더 패턴 구현을 자동 생성
public class ErrorResponse {
    // 에러 발생 시간을 저장 (자동으로 현재 시간이 설정됨)
    private final LocalDateTime timestamp = LocalDateTime.now();

    // HTTP 상태 코드 (예: 400, 404, 500 등)
    private final int status;

    // HTTP 상태 메시지 (예: "Bad Request", "Not Found" 등)
    private final String error;

    // 애플리케이션의 에러 코드 (ErrorCode enum의 이름)
    private final String code;

    // 구체적인 에러 메시지
    private final String message;

    // 에러가 발생한 요청 URI
    private final String path;

    /**
     * ErrorResponse 객체를 생성하는 정적 팩토리 메서드
     * 
     * @param status    HTTP 상태
     * @param errorCode 에러 코드 enum
     * @param path      요청 URI
     * @return 생성된 ErrorResponse 객체
     */
    public static ErrorResponse of(HttpStatus status, ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .status(status.value()) // HTTP 상태 코드
                .error(status.getReasonPhrase()) // HTTP 상태 메시지
                .code(errorCode.name()) // 에러 코드 이름
                .message(errorCode.getMessage()) // 에러 메시지
                .path(path) // 요청 URI
                .build();
    }
}
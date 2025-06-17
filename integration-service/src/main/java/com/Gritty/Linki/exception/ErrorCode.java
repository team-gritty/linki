package com.Gritty.Linki.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션에서 사용되는 에러 코드를 정의하는 열거형 클래스
 * 각 에러 코드는 HTTP 상태와 에러 메시지를 가짐
 * 
 * 사용 예시:
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 * throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 이메일 형식입니다.");
 */
@Getter
public enum ErrorCode {
    // 클라이언트 오류 (4xx)
    // 400 Bad Request - 잘못된 요청
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid input value"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "Invalid type value"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "Invalid file format"),

    // 401 Unauthorized - 인증 실패
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),

    // 403 Forbidden - 권한 없음
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden access"),

    // 404 Not Found - 리소스를 찾을 수 없음
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),

    // 405 Method Not Allowed - 허용되지 않는 HTTP 메서드
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Invalid method type"),

    // 409 Conflict - 리소스 충돌
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Resource already exists"),

    // 서버 오류 (5xx)
    // 500 Internal Server Error - 서버 내부 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed"), CAMPAIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 캠페인을 찾을 수 없습니다." ),;

    // HTTP 상태 코드와 이에 대응하는 상태 메시지를 포함하는 HttpStatus 객체
    private final HttpStatus status;

    // 클라이언트에게 보여줄 에러 메시지
    private final String message;
    

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
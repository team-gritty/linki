package com.Gritty.Linki.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러 클래스
 * 각 예외 타입별로 적절한 에러 응답을 생성하여 클라이언트에게 반환
 * 
 * 주요 기능:
 * - 비즈니스 예외 처리 (@BusinessException)
 * - 입력값 검증 실패 처리 (@Valid, @Validated)
 * - 파일 업로드 예외 처리
 * - 기타 예외 처리
 */
@Slf4j // Lombok: 로깅을 위한 log 변수를 자동으로 생성
@RestControllerAdvice // 모든 컨트롤러에서 발생하는 예외를 처리하기 위한 어노테이션
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     * 비즈니스 로직에서 발생하는 예외를 처리
     * 
     * @param e       발생한 BusinessException
     * @param request 현재 처리 중인 HTTP 요청
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode.getStatus(), errorCode, request.getRequestURI());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * MethodArgumentNotValidException 처리
     * 
     * @Valid 또는 @Validated 어노테이션으로 검증 실패 시 발생하는 예외를 처리
     * 
     * @param e       발생한 MethodArgumentNotValidException
     * @param request 현재 처리 중인 HTTP 요청
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation failed: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * MaxUploadSizeExceededException 처리
     * 파일 업로드 시 최대 허용 크기를 초과한 경우 발생하는 예외를 처리
     * 
     * @param e       발생한 MaxUploadSizeExceededException
     * @param request 현재 처리 중인 HTTP 요청
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.error("File size exceeded: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_FILE_FORMAT,
                request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * BindException 처리
     * 객체 바인딩 실패 시 발생하는 예외를 처리
     * 
     * @param e       발생한 BindException
     * @param request 현재 처리 중인 HTTP 요청
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException e, HttpServletRequest request) {
        log.warn("Binding failed: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_INPUT_VALUE,
                request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 기타 예외 처리
     * 위에서 처리되지 않은 모든 예외를 처리
     * 
     * @param e       발생한 Exception
     * @param request 현재 처리 중인 HTTP 요청
     * @return ErrorResponse를 포함한 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", e);
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getRequestURI());
        return ResponseEntity.internalServerError().body(response);
    }
}

/*
 * 사용 예시:
 * 
 * 1. 비즈니스 로직에서 예외 발생시
 * if (user == null) {
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 * }
 * 
 * 2. 커스텀 메시지와 함께 예외 발생
 * if (!isValidFile(file)) {
 * throw new BusinessException(
 * ErrorCode.INVALID_FILE_FORMAT,
 * "지원하지 않는 파일 형식입니다: " + file.getContentType()
 * );
 * }
 */
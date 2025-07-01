package com.ssg.chatservice.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전역 예외 처리 핸들러입니다.
 * 각종 예외 발생 시 일관된 형식으로 에러 응답을 제공합니다.
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 모든 종류의 예외(Exception)를 처리합니다.
     * 서버 내부 에러가 발생한 경우 클라이언트에게 500 상태코드와 에러 메시지를 반환합니다.
     *
     * @param e Exception 예외 객체
     * @return 500 상태 코드와 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류: " + e.getMessage());
    }

    /**
     * 잘못된 인자가 전달될 때 발생하는 IllegalArgumentException 예외를 처리합니다.
     * 클라이언트 요청 파라미터가 유효하지 않을 경우 400 상태코드를 반환합니다.
     *
     * @param e IllegalArgumentException 예외 객체
     * @return 400 상태 코드와 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("잘못된 요청: " + e.getMessage());
    }

    /**
     * NullPointerException이 발생했을 때 처리합니다.
     * 주로 객체 참조가 누락된 경우 발생하며, 500 상태코드를 반환합니다.
     *
     * @param e NullPointerException 예외 객체
     * @return 500 상태 코드와 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNPE(NullPointerException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 오류(NPE): " + e.getMessage());
    }

    /**
     * @Valid 어노테이션이 붙은 요청 객체에서 유효성 검증에 실패했을 경우 발생하는 예외를 처리합니다.
     * 주로 클라이언트 요청 파라미터가 형식에 맞지 않거나, 누락되었을 때 발생합니다.
     * HTTP 상태코드 400 (BAD_REQUEST)와 함께 에러 메시지를 응답합니다.
     *
     * @param e MethodArgumentNotValidException 예외 객체
     * @return 400 상태 코드와 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException e) {
        log.warn("검증 실패: {}", e.getMessage());
        return ResponseEntity.badRequest().body("입력값이 유효하지 않습니다.");
    }
}
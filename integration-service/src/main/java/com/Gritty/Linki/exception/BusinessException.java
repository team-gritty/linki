package com.Gritty.Linki.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외를 처리하기 위한 사용자 정의 예외 클래스
 * RuntimeException을 상속받아 Unchecked Exception으로 구현
 * 
 * 사용 예시:
 * 1. 기본 에러 메시지 사용:
 * if (user == null) {
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 * }
 * 
 * 2. 커스텀 에러 메시지 사용:
 * if (!isValidFile(file)) {
 * throw new BusinessException(
 * ErrorCode.INVALID_FILE_FORMAT,
 * "지원하지 않는 파일 형식입니다: " + file.getContentType()
 * );
 * }
 */
@Getter // Lombok: errorCode의 getter 메서드를 자동 생성
public class BusinessException extends RuntimeException {
    // 발생한 에러의 종류를 구분하기 위한 ErrorCode
    private final ErrorCode errorCode;

    /**
     * ErrorCode만을 받는 생성자
     * ErrorCode에 정의된 기본 메시지를 사용합니다.
     * 
     * @param errorCode 발생한 에러의 종류
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 상위 클래스의 생성자를 호출하여 기본 에러 메시지 설정
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 커스텀 메시지를 받는 생성자
     * 기본 메시지 대신 상세한 커스텀 메시지를 사용할 수 있습니다.
     * 
     * @param errorCode 발생한 에러의 종류
     * @param message   커스텀 에러 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message); // 상위 클래스의 생성자를 호출하여 커스텀 에러 메시지 설정
        this.errorCode = errorCode;
    }
}
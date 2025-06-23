package com.Gritty.Linki.domain.account.account.service;

public interface FindPasswordService {
    
    /**
     * 인증번호 발송
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @return 발송 성공 여부
     */
    boolean sendVerificationCode(String userName, String userLoginId, String userEmail);
    
    /**
     * 인증번호 확인
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @param verificationCode 인증번호
     * @return 확인 성공 여부
     */
    boolean verifyCode(String userName, String userLoginId, String userEmail, String verificationCode);
    
    /**
     * 인증번호 재발송
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @return 재발송 성공 여부
     */
    boolean resendVerificationCode(String userName, String userLoginId, String userEmail);
    
    /**
     * 비밀번호 변경
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @param verificationCode 인증번호
     * @param newPassword 새 비밀번호
     * @return 변경 성공 여부
     */
    boolean changePassword(String userName, String userLoginId, String userEmail, String verificationCode, String newPassword);
    
    /**
     * 디버깅용: 사용자 정보 확인
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @return 사용자 존재 여부
     */
    boolean checkUser(String userName, String userLoginId, String userEmail);
} 
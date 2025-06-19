package com.Gritty.Linki.domain.user.User.service;

import com.Gritty.Linki.domain.user.User.dto.FindIdResponseDto;

public interface FindIdService {
    
    /**
     * 인증번호 발송
     * @param userName 사용자 이름
     * @param userEmail 사용자 이메일
     * @return 발송 성공 여부
     */
    boolean sendVerificationCode(String userName, String userEmail);
    
    /**
     * 인증번호 확인 및 아이디 반환
     * @param userName 사용자 이름
     * @param userEmail 사용자 이메일
     * @param verificationCode 인증번호
     * @return 사용자 정보 (인증 성공 시)
     */
    FindIdResponseDto verifyCode(String userName, String userEmail, String verificationCode);
    
    /**
     * 인증번호 재발송
     * @param userName 사용자 이름
     * @param userEmail 사용자 이메일
     * @return 재발송 성공 여부
     */
    boolean resendVerificationCode(String userName, String userEmail);
    
    /**
     * 디버깅용: 사용자 정보 확인
     * @param userName 사용자 이름
     * @param userEmail 사용자 이메일
     * @return 사용자 ID (없으면 null)
     */
    String checkUser(String userName, String userEmail);
} 
package com.Gritty.Linki.oAuth.account.service;

import com.Gritty.Linki.domain.oAuth.account.repository.FindPasswordRepository;
import com.Gritty.Linki.domain.oAuth.account.service.FindPasswordServiceImpl;
import com.Gritty.Linki.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPasswordServiceTest {

    @Mock
    private FindPasswordRepository findPasswordRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FindPasswordServiceImpl findPasswordService;

    private User testUser;
    private final String userName = "테스트유저";
    private final String userLoginId = "testuser";
    private final String userEmail = "test@example.com";
    private final String userPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName(userName);
        testUser.setUserLoginId(userLoginId);
        testUser.setUserEmail(userEmail);
        testUser.setUserLoginPw(userPassword);
    }

    @Test
    @DisplayName("인증번호 발송 성공 테스트")
    void sendVerificationCode_Success() {
        // given
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());

        // when
        boolean result = findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // then
        assertTrue(result);
        verify(findPasswordRepository).findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
        verify(mailSender).send(any());
    }

    @Test
    @DisplayName("인증번호 발송 실패 - 사용자 정보 없음")
    void sendVerificationCode_Failure_UserNotFound() {
        // given
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(null);

        // when
        boolean result = findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // then
        assertFalse(result);
        verify(findPasswordRepository).findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
        verify(mailSender, never()).send(any());
    }

    @Test
    @DisplayName("인증번호 확인 성공 테스트")
    void verifyCode_Success() {
        // given
        String verificationCode = "123456";
        
        // 먼저 인증번호를 발송
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());
        findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // when
        boolean result = findPasswordService.verifyCode(userName, userLoginId, userEmail, verificationCode);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("인증번호 확인 실패 - 잘못된 인증번호")
    void verifyCode_Failure_WrongCode() {
        // given
        String verificationCode = "123456";
        String wrongCode = "654321";
        
        // 먼저 인증번호를 발송
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());
        findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // when
        boolean result = findPasswordService.verifyCode(userName, userLoginId, userEmail, wrongCode);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("인증번호 재발송 성공 테스트")
    void resendVerificationCode_Success() {
        // given
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());

        // when
        boolean result = findPasswordService.resendVerificationCode(userName, userLoginId, userEmail);

        // then
        assertTrue(result);
        verify(findPasswordRepository, times(1)).findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
        verify(mailSender, times(1)).send(any());
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void changePassword_Success() {
        // given
        String verificationCode = "123456";
        String newPassword = "newPassword123";
        
        // 먼저 인증번호를 발송하고 확인
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());
        when(passwordEncoder.matches(newPassword, userPassword)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(findPasswordRepository.save(any(User.class))).thenReturn(testUser);
        
        findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // when
        boolean result = findPasswordService.changePassword(userName, userLoginId, userEmail, verificationCode, newPassword);

        // then
        assertTrue(result);
        verify(passwordEncoder).encode(newPassword);
        verify(findPasswordRepository).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호와 동일")
    void changePassword_Failure_SamePassword() {
        // given
        String verificationCode = "123456";
        String newPassword = "newPassword123";
        
        // 먼저 인증번호를 발송
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());
        when(passwordEncoder.matches(newPassword, userPassword)).thenReturn(true);
        
        findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // when
        boolean result = findPasswordService.changePassword(userName, userLoginId, userEmail, verificationCode, newPassword);

        // then
        assertFalse(result);
        verify(passwordEncoder, never()).encode(any());
        verify(findPasswordRepository, never()).save(any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 비밀번호가 너무 짧음")
    void changePassword_Failure_PasswordTooShort() {
        // given
        String verificationCode = "123456";
        String shortPassword = "123";
        
        // 먼저 인증번호를 발송
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);
        doNothing().when(mailSender).send(any());
        
        findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);

        // when
        boolean result = findPasswordService.changePassword(userName, userLoginId, userEmail, verificationCode, shortPassword);

        // then
        assertFalse(result);
        verify(passwordEncoder, never()).encode(any());
        verify(findPasswordRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 정보 확인 성공 테스트")
    void checkUser_Success() {
        // given
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(testUser);

        // when
        boolean result = findPasswordService.checkUser(userName, userLoginId, userEmail);

        // then
        assertTrue(result);
        verify(findPasswordRepository).findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
    }

    @Test
    @DisplayName("사용자 정보 확인 실패 테스트")
    void checkUser_Failure() {
        // given
        when(findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail))
                .thenReturn(null);

        // when
        boolean result = findPasswordService.checkUser(userName, userLoginId, userEmail);

        // then
        assertFalse(result);
        verify(findPasswordRepository).findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
    }
} 
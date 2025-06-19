package com.Gritty.Linki.domain.user.User.service;

import com.Gritty.Linki.domain.user.User.dto.FindIdResponseDto;
import com.Gritty.Linki.domain.user.User.repository.FindIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindIdServiceTest {

    @Mock
    private FindIdRepository findIdRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private FindIdServiceImpl findIdService;

    private static final String TEST_NAME = "김라떼1";
    private static final String TEST_EMAIL = "kimsj0118@naver.com";
    private static final String TEST_USER_ID = "qwer";

    @BeforeEach
    void setUp() {
        // 테스트 전 초기화 작업
    }

    @Test
    @DisplayName("인증번호 발송 - 성공")
    void sendVerificationCode_Success() {
        // given
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(TEST_USER_ID);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        boolean result = findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isTrue();
        verify(findIdRepository).findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증번호 발송 - 사용자 정보 없음")
    void sendVerificationCode_UserNotFound() {
        // given
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(null);

        // when
        boolean result = findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isFalse();
        verify(findIdRepository).findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증번호 확인 - 성공")
    void verifyCode_Success() {
        // given
        String verificationCode = "123456";
        
        // 먼저 인증번호 발송
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(TEST_USER_ID);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // when
        FindIdResponseDto result = findIdService.verifyCode(TEST_NAME, TEST_EMAIL, verificationCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("인증번호 확인 - 잘못된 인증번호")
    void verifyCode_WrongCode() {
        // given
        String wrongCode = "999999";
        
        // 먼저 인증번호 발송
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(TEST_USER_ID);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // when
        FindIdResponseDto result = findIdService.verifyCode(TEST_NAME, TEST_EMAIL, wrongCode);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("인증번호 확인 - 인증번호 없음")
    void verifyCode_NoCode() {
        // given
        String verificationCode = "123456";

        // when (인증번호 발송 없이 바로 확인)
        FindIdResponseDto result = findIdService.verifyCode(TEST_NAME, TEST_EMAIL, verificationCode);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("인증번호 재발송 - 성공")
    void resendVerificationCode_Success() {
        // given
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(TEST_USER_ID);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        boolean result = findIdService.resendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isTrue();
        verify(findIdRepository, times(1)).findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증번호 재발송 - 사용자 정보 없음")
    void resendVerificationCode_UserNotFound() {
        // given
        when(findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL))
                .thenReturn(null);

        // when
        boolean result = findIdService.resendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isFalse();
        verify(findIdRepository).findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
} 
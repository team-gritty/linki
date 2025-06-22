package com.Gritty.Linki.oAuth.account.repository;

import com.Gritty.Linki.domain.account.account.repository.FindIdRepository;
import com.Gritty.Linki.domain.account.account.service.FindIdServiceImpl;
import com.Gritty.Linki.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class FindIdRepositoryTest {

    @Autowired
    private FindIdRepository findIdRepository;

    @Mock
    private JavaMailSender mailSender;

    @Autowired
    private FindIdServiceImpl findIdService;

    private static final String TEST_NAME = "sdsdddd";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USER_ID = "sdfeeesdf";
    private static final String TEST_LOGIN_ID = "sssef";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        User user = User.builder()
                .userId(TEST_USER_ID)
                .userLoginId(TEST_LOGIN_ID)
                .userLoginPw("encodedPassword")
                .userName(TEST_NAME)
                .userPhone("010-1234-5678")
                .userEmail(TEST_EMAIL)
                .userPayStatus(0)
                .userStatus(1)
                .userEnterDay(LocalDate.now())
                .userRole("ROLE_USER")
                .build();

        findIdRepository.save(user);
    }

    @Test
    @DisplayName("FindIdRepository - 이름과 이메일로 사용자 ID 조회 - 성공")
    void findUserIdByNameAndEmail_Success() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_LOGIN_ID);
    }

    @Test
    @DisplayName("FindIdRepository - 존재하지 않는 사용자 조회 - 실패")
    void findUserIdByNameAndEmail_NotFound() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("FindIdRepository - 대소문자 무시 조회 - 성공")
    void findUserIdByNameAndEmail_CaseInsensitive() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(TEST_NAME.toUpperCase(), TEST_EMAIL.toUpperCase());

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_LOGIN_ID);
    }

    @Test
    @DisplayName("FindIdRepository - 공백 무시 조회 - 성공")
    void findUserIdByNameAndEmail_TrimWhitespace() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(" " + TEST_NAME + " ", " " + TEST_EMAIL + " ");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_LOGIN_ID);
    }

    @Test
    @DisplayName("FindIdService - 사용자 정보 확인 - 성공")
    void checkUser_Success() {
        // when
        String result = findIdService.checkUser("testId", "hong@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_LOGIN_ID);
    }

    @Test
    @DisplayName("FindIdService - 사용자 정보 확인 - 실패")
    void checkUser_NotFound() {
        // when
        String result = findIdService.checkUser("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("FindIdService - 인증번호 발송 - 성공")
    void sendVerificationCode_Success() {
        // given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        boolean result = findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isTrue();
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("FindIdService - 인증번호 발송 - 사용자 없음")
    void sendVerificationCode_UserNotFound() {
        // when
        boolean result = findIdService.sendVerificationCode("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isFalse();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("FindIdService - 인증번호 확인 - 성공")
    void verifyCode_Success() {
        // given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // 인증번호 가져오기 (실제로는 private 필드에 접근할 수 없으므로 다른 방법 필요)
        // 여기서는 실제 인증번호를 알 수 없으므로 테스트 구조만 보여줌

        // when & then
        // 실제 테스트는 인증번호를 알 수 있어야 하므로 별도 구현 필요
    }

    @Test
    @DisplayName("FindIdService - 인증번호 재발송 - 성공")
    void resendVerificationCode_Success() {
        // given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        boolean result = findIdService.resendVerificationCode("qwer", "kimsj0118@naver.com");

        // then
        assertThat(result).isTrue();
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("FindIdService - 인증번호 재발송 - 사용자 없음")
    void resendVerificationCode_UserNotFound() {
        // when
        boolean result = findIdService.resendVerificationCode("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isFalse();
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}

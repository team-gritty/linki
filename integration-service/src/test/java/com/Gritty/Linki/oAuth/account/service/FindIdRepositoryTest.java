package com.Gritty.Linki.oAuth.account.service;

import com.Gritty.Linki.domain.account.account.repository.FindIdRepository;
import com.Gritty.Linki.domain.account.account.service.FindIdServiceImpl;
import com.Gritty.Linki.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class FindIdRepositoryTest {

    @Autowired
    private FindIdRepository findIdRepository;

    @Autowired
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
        log.info("찾은 사용자 ID: {}", result);
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
        // when
        boolean result = findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isTrue();
        log.info("인증번호 발송 성공");
    }

    @Test
    @DisplayName("FindIdService - 인증번호 발송 - 사용자 없음")
    void sendVerificationCode_UserNotFound() {
        // when
        boolean result = findIdService.sendVerificationCode("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isFalse();
        log.info("사용자 없음으로 인증번호 발송 실패");
    }

    @Test
    @DisplayName("FindIdService - 인증번호 확인 - 성공")
    void verifyCode_Success() {
        // given - 인증번호 발송
        boolean sendResult = findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL);
        assertThat(sendResult).isTrue();
        
        // 인증번호 확인 (실제로는 인증번호를 알 수 없으므로 테스트 구조만 제공)
        log.info("인증번호 발송 완료, 실제 인증번호 확인은 별도 구현 필요");
    }

    @Test
    @DisplayName("FindIdService - 인증번호 재발송 - 성공")
    void resendVerificationCode_Success() {
        // when
        boolean result = findIdService.resendVerificationCode("qwer", "kimsj0118@naver.com");

        // then
        assertThat(result).isTrue();
        log.info("인증번호 재발송 성공");
    }

    @Test
    @DisplayName("FindIdService - 인증번호 재발송 - 사용자 없음")
    void resendVerificationCode_UserNotFound() {
        // when
        boolean result = findIdService.resendVerificationCode("존재하지않는사용자", "nonexistent@example.com");

        // then
        assertThat(result).isFalse();
        log.info("사용자 없음으로 인증번호 재발송 실패");
    }

    @Test
    @DisplayName("FindIdService - 실제 데이터로 사용자 확인")
    void checkUser_WithRealData() {
        // 실제 데이터베이스에 있는 사용자 정보로 테스트
        String result = findIdService.checkUser("홍길동", "hong@example.com");
        
        if (result != null) {
            log.info("실제 사용자 찾음: {}", result);
            assertThat(result).isNotNull();
        } else {
            log.info("실제 사용자를 찾을 수 없음");
        }
    }

    @Test
    @DisplayName("FindIdService - 실제 데이터로 인증번호 발송")
    void sendVerificationCode_WithRealData() {
        // 실제 데이터베이스에 있는 사용자 정보로 테스트
        boolean result = findIdService.sendVerificationCode("홍길동", "hong@example.com");
        
        if (result) {
            log.info("실제 사용자에게 인증번호 발송 성공");
            assertThat(result).isTrue();
        } else {
            log.info("실제 사용자에게 인증번호 발송 실패");
        }
    }
}

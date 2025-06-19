package com.Gritty.Linki.domain.user.User.repository;

import com.Gritty.Linki.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FindIdRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FindIdRepository findIdRepository;

    private static final String TEST_NAME = "테스트유저";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USER_ID = "testuser123";
    private static final String TEST_LOGIN_ID = "testuser";

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

        entityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("이름과 이메일로 사용자 ID 조회 - 성공")
    void findUserIdByNameAndEmail_Success() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(TEST_NAME, TEST_EMAIL);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_LOGIN_ID);
    }

    @Test
    @DisplayName("이름과 이메일로 사용자 ID 조회 - 이름 불일치")
    void findUserIdByNameAndEmail_WrongName() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail("잘못된이름", TEST_EMAIL);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("이름과 이메일로 사용자 ID 조회 - 이메일 불일치")
    void findUserIdByNameAndEmail_WrongEmail() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(TEST_NAME, "wrong@example.com");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("이름과 이메일로 사용자 ID 조회 - 모두 불일치")
    void findUserIdByNameAndEmail_BothWrong() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail("잘못된이름", "wrong@example.com");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("대소문자 구분 테스트")
    void findUserIdByNameAndEmail_CaseSensitive() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(TEST_NAME.toUpperCase(), TEST_EMAIL);

        // then
        assertThat(result).isNull(); // 대소문자가 다르므로 null 반환
    }

    @Test
    @DisplayName("공백 포함 테스트")
    void findUserIdByNameAndEmail_WithSpaces() {
        // when
        String result = findIdRepository.findUserIdByNameAndEmail(" " + TEST_NAME + " ", TEST_EMAIL);

        // then
        assertThat(result).isNull(); // 공백이 포함되어 있으므로 null 반환
    }
} 
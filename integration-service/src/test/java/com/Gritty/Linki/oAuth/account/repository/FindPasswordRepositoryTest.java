package com.Gritty.Linki.oAuth.account.repository;

import com.Gritty.Linki.domain.account.account.repository.FindPasswordRepository;
import com.Gritty.Linki.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FindPasswordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FindPasswordRepository findPasswordRepository;

    private User testUser;
    private final String userName = "테스트유저";
    private final String userLoginId = "testuser";
    private final String userEmail = "test@example.com";
    private final String userPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        testUser = new User();
        testUser.setUserName(userName);
        testUser.setUserLoginId(userLoginId);
        testUser.setUserEmail(userEmail);
        testUser.setUserLoginPw(userPassword);
        
        // 데이터베이스에 저장
        entityManager.persistAndFlush(testUser);
    }

    @Test
    @DisplayName("이름, 아이디, 이메일로 사용자 조회 성공 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_Success() {
        // when
        User foundUser = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);

        // then
        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getUserName());
        assertEquals(userLoginId, foundUser.getUserLoginId());
        assertEquals(userEmail, foundUser.getUserEmail());
    }

    @Test
    @DisplayName("이름, 아이디, 이메일로 사용자 조회 실패 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_Failure() {
        // when
        User foundUser = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail("존재하지않는이름", userLoginId, userEmail);

        // then
        assertNull(foundUser);
    }

    @Test
    @DisplayName("대소문자 무시 조회 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_CaseInsensitive() {
        // when
        User foundUser = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(
            userName.toUpperCase(), 
            userLoginId.toUpperCase(), 
            userEmail.toUpperCase()
        );

        // then
        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getUserName());
        assertEquals(userLoginId, foundUser.getUserLoginId());
        assertEquals(userEmail, foundUser.getUserEmail());
    }

    @Test
    @DisplayName("공백 무시 조회 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_TrimWhitespace() {
        // when
        User foundUser = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(
            " " + userName + " ", 
            " " + userLoginId + " ", 
            " " + userEmail + " "
        );

        // then
        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getUserName());
        assertEquals(userLoginId, foundUser.getUserLoginId());
        assertEquals(userEmail, foundUser.getUserEmail());
    }

    @Test
    @DisplayName("기존 메서드 - 아이디와 이메일로 사용자 조회 성공 테스트")
    void findByUserLoginIdAndUserEmail_Success() {
        // when
        User foundUser = findPasswordRepository.findByUserLoginIdAndUserEmail(userLoginId, userEmail);

        // then
        assertNotNull(foundUser);
        assertEquals(userName, foundUser.getUserName());
        assertEquals(userLoginId, foundUser.getUserLoginId());
        assertEquals(userEmail, foundUser.getUserEmail());
    }

    @Test
    @DisplayName("기존 메서드 - 아이디와 이메일로 사용자 조회 실패 테스트")
    void findByUserLoginIdAndUserEmail_Failure() {
        // when
        User foundUser = findPasswordRepository.findByUserLoginIdAndUserEmail("존재하지않는아이디", userEmail);

        // then
        assertNull(foundUser);
    }

    @Test
    @DisplayName("부분 일치로는 조회되지 않는지 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_PartialMatch() {
        // when - 이름만 일치
        User foundUser1 = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, "wrongid", userEmail);
        
        // when - 아이디만 일치
        User foundUser2 = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail("wrongname", userLoginId, userEmail);
        
        // when - 이메일만 일치
        User foundUser3 = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, "wrong@email.com");

        // then
        assertNull(foundUser1);
        assertNull(foundUser2);
        assertNull(foundUser3);
    }

    @Test
    @DisplayName("여러 사용자가 있을 때 정확한 조회 테스트")
    void findByUserNameAndUserLoginIdAndUserEmail_MultipleUsers() {
        // given - 추가 사용자 생성
        User anotherUser = new User();
        anotherUser.setUserName("다른유저");
        anotherUser.setUserLoginId("anotheruser");
        anotherUser.setUserEmail("another@example.com");
        anotherUser.setUserLoginPw("anotherPassword");
        
        entityManager.persistAndFlush(anotherUser);

        // when
        User foundUser1 = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
        User foundUser2 = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail("다른유저", "anotheruser", "another@example.com");

        // then
        assertNotNull(foundUser1);
        assertEquals(userName, foundUser1.getUserName());
        
        assertNotNull(foundUser2);
        assertEquals("다른유저", foundUser2.getUserName());
    }
} 
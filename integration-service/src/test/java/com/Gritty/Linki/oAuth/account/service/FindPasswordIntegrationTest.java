//package com.Gritty.Linki.oAuth.account.service;
//
//import com.Gritty.Linki.domain.oAuth.account.repository.FindPasswordRepository;
//import com.Gritty.Linki.domain.oAuth.account.service.FindPasswordServiceImpl;
//import com.Gritty.Linki.entity.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//
//@SpringBootTest
//@Transactional
//@ActiveProfiles("test")
//class FindPasswordIntegrationTest {
//
//    @Autowired
//    private FindPasswordRepository findPasswordRepository;
//
//    @MockBean
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private FindPasswordServiceImpl findPasswordService;
//
//    private User testUser;
//    private final String userName = "테스트유저";
//    private final String userLoginId = "testuser";
//    private final String userEmail = "test@example.com";
//    private final String userPassword = "testPassword123";
//
//    @BeforeEach
//    void setUp() {
//        // 테스트 데이터 생성 및 저장
//        testUser = new User();
//        testUser.setUserName(userName);
//        testUser.setUserLoginId(userLoginId);
//        testUser.setUserEmail(userEmail);
//        testUser.setUserLoginPw(passwordEncoder.encode(userPassword));
//
//        findPasswordRepository.save(testUser);
//
//        // 이메일 발송 모킹
//        doNothing().when(mailSender).send(any());
//    }
//
//    @Test
//    @DisplayName("전체 비밀번호 찾기 플로우 통합 테스트")
//    void completeFindPasswordFlow_Test() {
//        // 1단계: 인증번호 발송
//        boolean sendResult = findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);
//        assertTrue(sendResult, "인증번호 발송이 성공해야 합니다.");
//
//        // 2단계: 인증번호 확인 (실제 발송된 인증번호는 알 수 없으므로, 발송 후 바로 확인)
//        // 실제 구현에서는 인증번호를 저장소에서 가져와야 하지만, 테스트에서는 발송된 인증번호를 사용
//        // 여기서는 발송 성공 여부만 확인
//
//        // 3단계: 사용자 정보 확인
//        boolean checkResult = findPasswordService.checkUser(userName, userLoginId, userEmail);
//        assertTrue(checkResult, "사용자 정보 확인이 성공해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("잘못된 사용자 정보로 인증번호 발송 실패 테스트")
//    void sendVerificationCodeWithInvalidUser_Test() {
//        // given
//        String wrongUserName = "잘못된이름";
//        String wrongUserLoginId = "wrongid";
//        String wrongUserEmail = "wrong@email.com";
//
//        // when
//        boolean result = findPasswordService.sendVerificationCode(wrongUserName, wrongUserLoginId, wrongUserEmail);
//
//        // then
//        assertFalse(result, "잘못된 사용자 정보로는 인증번호 발송이 실패해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("부분적으로 잘못된 사용자 정보 테스트")
//    void sendVerificationCodeWithPartialInvalidInfo_Test() {
//        // 이름만 잘못된 경우
//        boolean result1 = findPasswordService.sendVerificationCode("잘못된이름", userLoginId, userEmail);
//        assertFalse(result1, "이름이 잘못되면 인증번호 발송이 실패해야 합니다.");
//
//        // 아이디만 잘못된 경우
//        boolean result2 = findPasswordService.sendVerificationCode(userName, "잘못된아이디", userEmail);
//        assertFalse(result2, "아이디가 잘못되면 인증번호 발송이 실패해야 합니다.");
//
//        // 이메일만 잘못된 경우
//        boolean result3 = findPasswordService.sendVerificationCode(userName, userLoginId, "wrong@email.com");
//        assertFalse(result3, "이메일이 잘못되면 인증번호 발송이 실패해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("대소문자 및 공백 무시 테스트")
//    void caseInsensitiveAndTrimTest() {
//        // 대소문자와 공백이 포함된 정보로 테스트
//        boolean result = findPasswordService.sendVerificationCode(
//            " " + userName.toUpperCase() + " ",
//            " " + userLoginId.toUpperCase() + " ",
//            " " + userEmail.toUpperCase() + " "
//        );
//
//        assertTrue(result, "대소문자와 공백을 무시하고 인증번호 발송이 성공해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("인증번호 재발송 테스트")
//    void resendVerificationCode_Test() {
//        // 첫 번째 발송
//        boolean firstSend = findPasswordService.sendVerificationCode(userName, userLoginId, userEmail);
//        assertTrue(firstSend, "첫 번째 인증번호 발송이 성공해야 합니다.");
//
//        // 재발송
//        boolean resend = findPasswordService.resendVerificationCode(userName, userLoginId, userEmail);
//        assertTrue(resend, "인증번호 재발송이 성공해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("사용자 정보 확인 성공 테스트")
//    void checkUser_Success_Test() {
//        // when
//        boolean result = findPasswordService.checkUser(userName, userLoginId, userEmail);
//
//        // then
//        assertTrue(result, "올바른 사용자 정보로 확인이 성공해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("사용자 정보 확인 실패 테스트")
//    void checkUser_Failure_Test() {
//        // when
//        boolean result = findPasswordService.checkUser("존재하지않는이름", "존재하지않는아이디", "존재하지않는@이메일.com");
//
//        // then
//        assertFalse(result, "존재하지 않는 사용자 정보로는 확인이 실패해야 합니다.");
//    }
//
//    @Test
//    @DisplayName("Repository 메서드 직접 테스트")
//    void repositoryMethodTest() {
//        // 새로운 메서드 테스트
//        User foundUser = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
//        assertNotNull(foundUser, "새로운 메서드로 사용자를 찾을 수 있어야 합니다.");
//        assertEquals(userName, foundUser.getUserName());
//        assertEquals(userLoginId, foundUser.getUserLoginId());
//        assertEquals(userEmail, foundUser.getUserEmail());
//
//        // 기존 메서드 테스트
//        User foundUserOld = findPasswordRepository.findByUserLoginIdAndUserEmail(userLoginId, userEmail);
//        assertNotNull(foundUserOld, "기존 메서드로도 사용자를 찾을 수 있어야 합니다.");
//    }
//
//    @Test
//    @DisplayName("여러 사용자 환경에서의 테스트")
//    void multipleUsersTest() {
//        // 추가 사용자 생성
//        User anotherUser = new User();
//        anotherUser.setUserName("다른유저");
//        anotherUser.setUserLoginId("anotheruser");
//        anotherUser.setUserEmail("another@example.com");
//        anotherUser.setUserLoginPw(passwordEncoder.encode("anotherPassword"));
//
//        findPasswordRepository.save(anotherUser);
//
//        // 첫 번째 사용자 확인
//        boolean result1 = findPasswordService.checkUser(userName, userLoginId, userEmail);
//        assertTrue(result1, "첫 번째 사용자 확인이 성공해야 합니다.");
//
//        // 두 번째 사용자 확인
//        boolean result2 = findPasswordService.checkUser("다른유저", "anotheruser", "another@example.com");
//        assertTrue(result2, "두 번째 사용자 확인이 성공해야 합니다.");
//
//        // 잘못된 조합 확인
//        boolean result3 = findPasswordService.checkUser(userName, "anotheruser", userEmail);
//        assertFalse(result3, "잘못된 조합으로는 확인이 실패해야 합니다.");
//    }
//}
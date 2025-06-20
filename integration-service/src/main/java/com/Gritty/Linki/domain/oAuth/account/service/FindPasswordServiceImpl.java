package com.Gritty.Linki.domain.oAuth.account.service;

import com.Gritty.Linki.domain.oAuth.account.repository.FindPasswordRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindPasswordServiceImpl implements FindPasswordService {

    private final FindPasswordRepository findPasswordRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    
    // 인증번호 저장소 (실제로는 Redis나 DB를 사용하는 것이 좋습니다)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> verificationTimestamps = new ConcurrentHashMap<>();
    
    private static final long VERIFICATION_EXPIRE_TIME = 3 * 60 * 1000; // 3분
    private static final String EMAIL_SUBJECT = "[LINKI] 비밀번호 찾기 인증번호";
    
    @Override
    public boolean sendVerificationCode(String userName, String userLoginId, String userEmail) {
        try {
            log.info("비밀번호 찾기 인증번호 발송 요청 - userName: '{}', userLoginId: '{}', userEmail: '{}'", userName, userLoginId, userEmail);
            
            // 사용자 정보 확인 (이름, 아이디, 이메일 모두 일치하는지 확인)
            User user = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
            log.info("데이터베이스 조회 결과 - user: {}", user != null ? "존재" : "없음");
            
            if (user == null) {
                log.warn("일치하는 사용자 정보를 찾을 수 없음: userName='{}', userLoginId='{}', userEmail='{}'", userName, userLoginId, userEmail);
                return false;
            }
            
            // 인증번호 생성 (6자리)
            String verificationCode = generateVerificationCode();
            
            // 인증번호 저장
            String key = userEmail + "_" + userLoginId;
            verificationCodes.put(key, verificationCode);
            verificationTimestamps.put(key, System.currentTimeMillis());
            
            // 이메일 발송
            sendVerificationEmail(userEmail, verificationCode);
            
            log.info("비밀번호 찾기 인증번호 발송 완료: userName={}, userEmail={}, userLoginId={}", userName, userEmail, userLoginId);
            return true;
            
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증번호 발송 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String userName, String userLoginId, String userEmail, String verificationCode) {
        try {
            String key = userEmail + "_" + userLoginId;
            
            // 인증번호 확인
            String storedCode = verificationCodes.get(key);
            Long timestamp = verificationTimestamps.get(key);
            
            if (storedCode == null || timestamp == null) {
                log.warn("저장된 인증번호가 없음: userEmail={}", userEmail);
                return false;
            }
            
            // 만료 시간 확인
            if (System.currentTimeMillis() - timestamp > VERIFICATION_EXPIRE_TIME) {
                log.warn("인증번호 만료: userEmail={}", userEmail);
                verificationCodes.remove(key);
                verificationTimestamps.remove(key);
                return false;
            }
            
            // 인증번호 일치 확인
            if (!storedCode.equals(verificationCode)) {
                log.warn("인증번호 불일치: userEmail={}", userEmail);
                return false;
            }
            
            log.info("비밀번호 찾기 인증번호 확인 성공: userName={}, userEmail={}", userName, userEmail);
            return true;
                    
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증번호 확인 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public boolean resendVerificationCode(String userName, String userLoginId, String userEmail) {
        try {
            // 기존 인증번호 삭제
            String key = userEmail + "_" + userLoginId;
            verificationCodes.remove(key);
            verificationTimestamps.remove(key);
            
            // 새로운 인증번호 발송
            return sendVerificationCode(userName, userLoginId, userEmail);
            
        } catch (Exception e) {
            log.error("비밀번호 찾기 인증번호 재발송 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public boolean changePassword(String userName, String userLoginId, String userEmail, String verificationCode, String newPassword) {
        try {
            // 인증번호 확인
            if (!verifyCode(userName, userLoginId, userEmail, verificationCode)) {
                log.warn("인증번호 확인 실패: userName={}, userLoginId={}, userEmail={}", userName, userLoginId, userEmail);
                return false;
            }
            
            // 사용자 정보 조회
            User user = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
            if (user == null) {
                log.warn("사용자 정보 조회 실패: userName={}, userLoginId={}, userEmail={}", userName, userLoginId, userEmail);
                return false;
            }
            
            // 새 비밀번호 검증
            if (newPassword == null || newPassword.length() < 6) {
                log.warn("새 비밀번호가 너무 짧음: userLoginId={}", userLoginId);
                return false;
            }
            
            // 현재 비밀번호와 동일한지 확인
            if (passwordEncoder.matches(newPassword, user.getUserLoginPw())) {
                log.warn("새 비밀번호가 현재 비밀번호와 동일함: userLoginId={}", userLoginId);
                return false;
            }
            
            // 비밀번호 변경
            user.setUserLoginPw(passwordEncoder.encode(newPassword));
            findPasswordRepository.save(user);
            
            // 인증번호 삭제 (사용 완료)
            String key = userEmail + "_" + userLoginId;
            verificationCodes.remove(key);
            verificationTimestamps.remove(key);
            
            log.info("비밀번호 변경 성공: userName={}, userLoginId={}, userEmail={}", userName, userLoginId, userEmail);
            return true;
            
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public boolean checkUser(String userName, String userLoginId, String userEmail) {
        try {
            log.info("사용자 정보 확인 - userName: '{}', userLoginId: '{}', userEmail: '{}'", userName, userLoginId, userEmail);
            User user = findPasswordRepository.findByUserNameAndUserLoginIdAndUserEmail(userName, userLoginId, userEmail);
            boolean exists = user != null;
            log.info("사용자 정보 확인 결과 - 존재: {}", exists);
            return exists;
        } catch (Exception e) {
            log.error("사용자 정보 확인 중 오류 발생", e);
            return false;
        }
    }
    
    /**
     * 6자리 인증번호 생성
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 인증번호 이메일 발송
     */
    private void sendVerificationEmail(String email, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(EMAIL_SUBJECT);
            message.setText("안녕하세요, LINKI입니다.\n\n" +
                    "비밀번호 찾기 인증번호는 [" + verificationCode + "] 입니다.\n" +
                    "인증번호는 3분간 유효합니다.\n\n" +
                    "감사합니다.");
            
            mailSender.send(message);
            log.info("비밀번호 찾기 인증번호 이메일 발송 완료: {}", email);
            
        } catch (Exception e) {
            log.error("이메일 발송 중 오류 발생: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
} 
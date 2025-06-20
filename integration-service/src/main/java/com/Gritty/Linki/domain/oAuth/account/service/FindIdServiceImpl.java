package com.Gritty.Linki.domain.oAuth.account.service;

import com.Gritty.Linki.domain.oAuth.dto.FindIdResponseDto;
import com.Gritty.Linki.domain.oAuth.account.repository.FindIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindIdServiceImpl implements FindIdService {

    private final FindIdRepository findIdRepository;
    private final JavaMailSender mailSender;
    
    // 인증번호 저장소 (실제로는 Redis나 DB를 사용하는 것이 좋습니다)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Long> verificationTimestamps = new ConcurrentHashMap<>();
    
    private static final long VERIFICATION_EXPIRE_TIME = 3 * 60 * 1000; // 3분
    private static final String EMAIL_SUBJECT = "[LINKI] 아이디 찾기 인증번호";
    
    @Override
    public boolean sendVerificationCode(String userName, String userEmail) {
        try {
            log.info("인증번호 발송 요청 - userName: '{}', userEmail: '{}'", userName, userEmail);
            
            // 사용자 정보 확인
            String userId = findIdRepository.findUserIdByNameAndEmail(userName, userEmail);
            log.info("데이터베이스 조회 결과 - userId: {}", userId);
            
            if (userId == null) {
                log.warn("일치하는 사용자 정보를 찾을 수 없음: userName='{}', userEmail='{}'", userName, userEmail);
                return false;
            }
            
            // 인증번호 생성 (6자리)
            String verificationCode = generateVerificationCode();
            
            // 인증번호 저장
            String key = userEmail + "_" + userName;
            verificationCodes.put(key, verificationCode);
            verificationTimestamps.put(key, System.currentTimeMillis());
            
            // 이메일 발송
            sendVerificationEmail(userEmail, verificationCode);
            
            log.info("인증번호 발송 완료: userEmail={}, userId={}", userEmail, userId);
            return true;
            
        } catch (Exception e) {
            log.error("인증번호 발송 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public FindIdResponseDto verifyCode(String userName, String userEmail, String verificationCode) {
        try {
            String key = userEmail + "_" + userName;
            
            // 인증번호 확인
            String storedCode = verificationCodes.get(key);
            Long timestamp = verificationTimestamps.get(key);
            
            if (storedCode == null || timestamp == null) {
                log.warn("저장된 인증번호가 없음: userEmail={}", userEmail);
                return null;
            }
            
            // 만료 시간 확인
            if (System.currentTimeMillis() - timestamp > VERIFICATION_EXPIRE_TIME) {
                log.warn("인증번호 만료: userEmail={}", userEmail);
                verificationCodes.remove(key);
                verificationTimestamps.remove(key);
                return null;
            }
            
            // 인증번호 일치 확인
            if (!storedCode.equals(verificationCode)) {
                log.warn("인증번호 불일치: userEmail={}", userEmail);
                return null;
            }
            
            // 사용자 정보 조회
            String userId = findIdRepository.findUserIdByNameAndEmail(userName, userEmail);
            if (userId == null) {
                log.warn("사용자 정보 조회 실패: userName={}, userEmail={}", userName, userEmail);
                return null;
            }
            
            // 인증번호 삭제 (사용 완료)
            verificationCodes.remove(key);
            verificationTimestamps.remove(key);
            
            log.info("인증번호 확인 성공: userEmail={}", userEmail);
            
            return FindIdResponseDto.builder()
                    .userId(userId)
                    .userName(userName)
                    .userEmail(userEmail)
                    .build();
                    
        } catch (Exception e) {
            log.error("인증번호 확인 중 오류 발생", e);
            return null;
        }
    }
    
    @Override
    public boolean resendVerificationCode(String userName, String userEmail) {
        try {
            // 기존 인증번호 삭제
            String key = userEmail + "_" + userName;
            verificationCodes.remove(key);
            verificationTimestamps.remove(key);
            
            // 새로운 인증번호 발송
            return sendVerificationCode(userName, userEmail);
            
        } catch (Exception e) {
            log.error("인증번호 재발송 중 오류 발생", e);
            return false;
        }
    }
    
    @Override
    public String checkUser(String userName, String userEmail) {
        try {
            log.info("사용자 정보 확인 - userName: '{}', userEmail: '{}'", userName, userEmail);
            String userId = findIdRepository.findUserIdByNameAndEmail(userName, userEmail);
            log.info("사용자 정보 확인 결과 - userId: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("사용자 정보 확인 중 오류 발생", e);
            return null;
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
                    "아이디 찾기 인증번호는 [" + verificationCode + "] 입니다.\n" +
                    "인증번호는 3분간 유효합니다.\n\n" +
                    "감사합니다.");
            
            mailSender.send(message);
            log.info("인증번호 이메일 발송 완료: {}", email);
            
        } catch (Exception e) {
            log.error("이메일 발송 중 오류 발생: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
} 
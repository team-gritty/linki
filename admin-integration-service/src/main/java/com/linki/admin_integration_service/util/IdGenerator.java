package com.linki.admin_integration_service.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private static final long EPOCH = 1704067200000L; // 2024-01-01 기준 (기준 시점)
    private static final long NODE_ID = 1L;

    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_NODE_ID = ~(-1L << NODE_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;

    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    private synchronized static String nextId() {
        long currentTimestamp = currentTime();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("시계가 뒤로 감겼음. ID 생성 실패");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 밀리초 바뀔 때까지 대기
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;
        long rawId = ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (NODE_ID << NODE_ID_SHIFT)
                | sequence;
        return Long.toString(rawId).substring(0, 16);
    }

    private static long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = currentTime();
        }
        return currentTimestamp;
    }

    private static long currentTime() {
        return System.currentTimeMillis();
    }

    // 회원 ID 생성
    public static String userId() {
        return "USR-" + nextId();
    }

    // 인증 ID 생성 (인플루언서 인증)
    public static String authId() { return "AUT-" + nextId(); }

    // 통계 ID 생성 (채널 상세정보)
    public static String statsId() { return "STD-" + nextId(); }

    // 채널 ID 생성
    public static String channelId() { return "CHN-" + nextId(); }

    // 인플루언서 ID 생성
    public static String influencerId() { return "INF-" + nextId(); }

    // 광고주 ID 생성
    public static String advertiserId() { return "ADV-" + nextId(); }

    // 캠페인 ID 생성
    public static String campaignId() { return "CMP-" + nextId(); }

    // 제안서 ID 생성
    public static String proposalId() { return "PRP-" + nextId(); }

    // 계약서 ID 생성
    public static String contractId() { return "CTR-" + nextId(); }

    // 전자서명 ID 생성
    public static String signatureId() { return "SIG-" + nextId(); }

    // 인플루언서 리뷰 ID 생성
    public static String influencerReviewId() { return "IRV-" + nextId(); }

    // 광고주 리뷰 ID 생성
    public static String advertiserReviewId() { return "ARV-" + nextId(); }

    // 관리자 ID 생성
    public static String adminId() { return "ADM-" + nextId(); }

    // 채팅방 ID 생성
    public static String chatId() { return "CHT-" + nextId(); }

    // 메세지 ID 생성
    public static String MessageId() { return "MSG-" + nextId(); }

    // 채팅 알람 ID 생성
    public static String chatAlarmId() { return "CAL-" + nextId(); }

    // 구독 ID 생성
    public static String subscribeId() { return "SUB-" + nextId(); }

    // 결제 ID 생성 (구독결제)
    public static String paymentId() { return "PAY-" + nextId(); }

    // 유저 결제키 ID 생성 (빌링)
    public static String billingId() { return "BIL-" + nextId(); }

    // 환불 ID 생성
    public static String refundId() { return "REF-" + nextId(); }

    // 토큰 ID 생성
    public static String jwtId() { return "JWT-" + nextId(); }

    // redirect Url 생성
    public static String redirectId() { return "RDR-" + nextId(); }

    // linkiScore 생성
    public static String linkiScoreId() { return "SCR-" + nextId(); }

    // 리다이렉트 클릭 생성
    public static String clickId() { return "CLI-" + nextId(); }

}

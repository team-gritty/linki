package com.ssg.chatservice.domain.chat.enums;

public enum NegoStatus {

    PENDING("제안서 대기"),                 // 처음 광고주가 보낸 상태
    ACCEPTED("제안서 승인"),               // 광고주가 제안서 수락 → 채팅방 활성화
    REJECTED("제안서 거절"),               // 광고주가 제안서 거절 → 채팅방 목록 필터링
    PENDING_SIGN("계약 서명 대기"),   // 협의 후 확정 → 서명 기다리는 상태
    ONGOING("계약 진행중"),            // 서명 완료 후 광고 진행 시작
    COMPLETED("계약 완료");               // 광고 이행 및 정산 완료

    private final String label;

    NegoStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

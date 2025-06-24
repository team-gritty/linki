package com.ssg.chatservice.domain.chat.enums;

public enum NegoStatus {

    PENDING,                 // 처음 광고주가 보낸 상태
    ACCEPTED,               // 광고주가 제안서 수락 → 채팅방 활성화
    REJECTED,               // 광고주가 제안서 거절 → 채팅방 목록 필터링
    PENDING_SIGN,   // 협의 후 확정 → 서명 기다리는 상태
    ONGOING,            // 서명 완료 후 광고 진행 시작
    COMPLETED;               // 광고 이행 및 정산 완료

}

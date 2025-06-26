package com.Gritty.Linki.domain.kafka.chat.enums;

import lombok.Getter;

@Getter
public enum EventType {
    PROPOSAL_CREATE("제안서 승인요청"),
    PROPOSAL_ACTIVE("제안서를 승인"),
    PROPOSAL_MODIFY("제안서를 수정"),
    PROPOSAL_REJECT("제안서를 거절"),
    PROPOSAL_DELETE("제안서를 삭제"),
    CONTRACT_CREATE("계약서를 생성"),
    CONTRACT_SIGN("계약서 서명을 완료"),
    CONTRACT_COMPLETED("정산을 완료");

    private final String label;

    EventType(String label) {
        this.label = label;
    }
}

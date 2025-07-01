package com.linki.admin_integration_service.vo.enums;

public enum SignatureStatus {
    PARTIALLY_SIGNED, // 한 명만 서명 완료
    BOTH_SIGNED,// 양쪽 모두 서명 완료
    REJECTED// 거절
}

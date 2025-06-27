package com.linki.admin_integration_service.vo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    BEAUTY("뷰티"),
    FASHION("패션"),
    SPORTS("스포츠"),
    FOOD("음식"),
    VLOG("브이로그"),
    TRAVEL("여행"),
    MUSIC("음악"),
    EDUCATION("교육"),
    ANIMAL("동물"),
    ELECTRONICS("전자제품"),
    ENTERTAINMENT("엔터테인먼트");

    private final String displayName;

    // 생성자 추가
    Category(String displayName) {
        this.displayName = displayName;
    }

    // getter 추가
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}

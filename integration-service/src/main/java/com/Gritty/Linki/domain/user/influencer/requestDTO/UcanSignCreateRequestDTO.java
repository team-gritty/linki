package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UcanSignCreateRequestDTO {
    private String templateId;
    private String documentName;
    private Boolean isSequential;       // 순차 서명 여부
    private Boolean isSendMessage;      // 메시지 전송 여부
    private String customValue1;        // 계약 ID 등 식별용

    private List<Map<String, Object>> fields;       // 필드 데이터
    private List<Map<String, Object>> participants; // 참여자 데이터
}

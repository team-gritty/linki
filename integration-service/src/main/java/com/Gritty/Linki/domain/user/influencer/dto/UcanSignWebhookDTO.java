package com.Gritty.Linki.domain.user.influencer.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UcanSignWebhookDTO {
    private String documentId;
    private String eventType;
    private String participantId;
    private String signatureStatus;
    private LocalDateTime timestamp;

    private String documentName;
    private String signingMethodType;
    private String customValue1;
    private String customValue2;
}

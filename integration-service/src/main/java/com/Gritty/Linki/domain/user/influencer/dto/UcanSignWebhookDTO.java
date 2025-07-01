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


}

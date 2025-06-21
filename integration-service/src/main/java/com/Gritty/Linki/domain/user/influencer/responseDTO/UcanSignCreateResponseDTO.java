package com.Gritty.Linki.domain.user.influencer.responseDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UcanSignCreateResponseDTO {
    private String msg;
    private UcanSignDocumentResult result;

    @Data
    public static class UcanSignDocumentResult {
        private String documentId;
        private String name;
        private String status;
        private Boolean expired;
        private String processType;
        private String createdAt;
    }
}

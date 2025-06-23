package com.Gritty.Linki.domain.user.influencer.responseDTO.campaign;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignCategoryResponseDTO {
    private String name; // ex: BEAUTY
    private String displayName; // ex: 뷰티
}

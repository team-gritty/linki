package com.Gritty.Linki.domain.user.advertiser.campaign.request;

import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 광고주가 캠페인 등록할때 주는 정보
 * Request 로 받기 위함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {

    @NotBlank(message = "캠페인 이름은 필수입니다")
    private String campaignName;

    @NotBlank(message = "캠페인 설명은 필수입니다")
    private String campaignDesc;

    private String campaignCondition;

    @NotBlank(message = "캠페인 이미지는 필수입니다")
    private String campaignImg;

    // Java Bean Validation(JSR-380)에서 제공하는 어노테이션 중 하나로, **유효성 검사(validation)**를 수행
    @NotNull(message = "캠페인 마감일은 필수입니다")
    private LocalDateTime campaignDeadline;

    @NotNull(message = "캠페인 공개 상태는 필수입니다")
    private CampaignPublishStatus campaignPublishStatus;

    @NotNull(message = "캠페인 카테고리는 필수입니다")
    private Category campaignCategory;
}
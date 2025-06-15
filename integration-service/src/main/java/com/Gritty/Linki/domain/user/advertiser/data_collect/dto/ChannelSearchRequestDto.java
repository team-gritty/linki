package com.Gritty.Linki.domain.user.advertiser.data_collect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채널 검색 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ChannelSearchRequestDto {

    @NotBlank(message = "검색 키워드는 필수입니다.")
    private String keyword;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @Positive(message = "최대 결과 수는 양수여야 합니다.")
    private Integer maxResults = 10;

    private String regionCode = "KR";

    public ChannelSearchRequestDto(String keyword, String category, Integer maxResults) {
        this.keyword = keyword;
        this.category = category;
        this.maxResults = maxResults;
    }
}
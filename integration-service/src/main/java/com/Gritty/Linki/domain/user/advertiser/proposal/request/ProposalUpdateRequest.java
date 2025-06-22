package com.Gritty.Linki.domain.user.advertiser.proposal.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 제안서 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalUpdateRequest {

    @NotBlank(message = "제안 내용은 필수입니다")
    private String contents; // 제안 내용
}
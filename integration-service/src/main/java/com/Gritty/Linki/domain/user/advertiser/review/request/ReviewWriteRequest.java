package com.Gritty.Linki.domain.user.advertiser.review.request;

import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewWriteRequest {

    @NotNull(message = "평점은 필수입니다.")
    @DecimalMin(value = "1.0", message = "평점은 1.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "평점은 5.0 이하여야 합니다.")
    private BigDecimal reviewScore;

    private String reviewComment;

}
